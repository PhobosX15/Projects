package bots.dbInteractor.xml;

import bots.dbInteractor.FileInteractor;
import bots.skill.template.Action;
import bots.skill.template.Skill;
import bots.skill.template.question.Question;
import bots.skill.template.slot.Slot;
import bots.skill.template.slot.SlotUnit;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class XmlInteractor extends FileInteractor {

    protected int skillIdIterator;
    protected final Document document;
    protected final Node rootNode;
    private Transformer transformer;
    protected final Map<Object, Integer> skillToId;

    /**
     * @param file Needs to be a .xml file
     */
    public XmlInteractor(File file) throws ParserConfigurationException, IOException, SAXException {
        super(file);

        // Building the document
        DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
        this.document = documentBuilder.parse(this.file);

        // Setting the root node as an instance variable for easy use.
        this.rootNode = this.document.getFirstChild();

        // Setting the id iterator to the amount of child nodes.
        NodeList skillList = ((Element) this.rootNode).getElementsByTagName("Skill");
        this.skillIdIterator = getHighestId(skillList) + 1;

        try {
            // Creates the transformer, used for saving changes.
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            this.transformer = transformerFactory.newTransformer(
                    new StreamSource(new File(file.getParent() + "\\xmlStyle.xslt")));

            // Makes sure that everything is indented correctly and that everything is printed on different lines.
            this.transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            this.transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            this.transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (TransformerConfigurationException e) {
            this.transformer = null;
        }
        this.skillToId = new HashMap<>();
    }

    /**
     * Increases the idIterator by 1, on top of the normal functionality.
     */
    @Override
    public void saveToFile(Skill skill) {
        // Creates the skill element and adding it to the root.
        Element newSkill = this.document.createElement("Skill");
        this.rootNode.appendChild(newSkill);

        // Setting the id of the root. This id should be unique for each skill.
        newSkill.setAttribute("id", Integer.toString(this.skillIdIterator));

        // Adds the skill to the skillToId map together with its iterator.
        this.skillToId.put(skill, this.skillIdIterator);

        // Creating the element for the question of the skill and adding it to the skill element.
        newSkill.appendChild(createQuestionElement(skill.getQuestion().toString()));

        // Creating the elements for the slots of the skill and adding it to the skill.
        newSkill.appendChild(createSlotsElement(skill.getSlots()));

        // Creating the elements for the actions of the skill and adding it to the skill.
        newSkill.appendChild(createActionsElement(skill.getActions()));

        // Save the changes to the file and increasing the iterator by 1.
        if (saveChanges()) {
            this.skillIdIterator++;
        }
    }

    @Override
    public List<Skill> convertFileToSkills() {
        List<Skill> skills = new LinkedList<>();

        // Gets all the nodes of the root. I.e. all the skills
        NodeList skillList = this.rootNode.getChildNodes();
        for (int i = 0; i < skillList.getLength(); i++) {
            Node skillNode = skillList.item(i);

            // Check if the found skill is of the right type, and whether it's name/tag is "Skill".
            // These should both normally be true.
            if (skillNode.getNodeType() == Node.ELEMENT_NODE && skillNode.getNodeName().equals("Skill")) {
                Element skillElement = (Element) skillNode;

                // Gets the question in the skill
                Node questionNode = skillElement.getElementsByTagName("Question").item(0);
                Question question = new Question(getAttributeTextFromNode(questionNode, "questionText"));

                // Gets all the slots in the skill
                List<String> slotNamesInQuestion = question.getSlotNames();
                List<String> slotNames = new LinkedList<>();
                List<Slot> slots = getSlots(skillElement, slotNamesInQuestion, slotNames);

                // Exception checking
                questionSlotsContainsAllWithError(slotNamesInQuestion, slotNames);

                List<Action> actions = getActions(skillElement, slots);

                // Puts the found skill in the skillToId map.
                Skill skill = new Skill(question, slots, actions);
                int skillId = Integer.parseInt(skillNode.getAttributes().getNamedItem("id").getTextContent());
                this.skillToId.put(skill, skillId);

                // Adds the found skill to the list of skills.
                skills.add(skill);
            }
        }
        return skills;
    }

    /**
     * Gets the list of slots in the given element.
     * @param skillElement The element of the skill where it needs to scan for actions.
     * @param slotNamesInQuestion The slots in the question.
     * @param slotNames This parameter gets changed in this method!
     *                  This is where all the slot names will be added to. This should preferably be empty.
     *                  But that is up to the one that implements it.
     * @return The list of slots it was able to find in the skill.
     * @throws IllegalArgumentException Gets thrown when a slot name is not in slots of the question.
     */
    protected List<Slot> getSlots(Element skillElement, List<String> slotNamesInQuestion, List<String> slotNames) throws IllegalArgumentException {
        NodeList slotsList = ((Element) skillElement.getElementsByTagName("Slots").item(0))
                .getElementsByTagName("Slot");
        List<Slot> slots = new LinkedList<>();
        for (int j = 0; j < slotsList.getLength(); j++) {
            Node slotNode = slotsList.item(j);
            String slotName = getAttributeTextFromNode(slotNode, "slotName");

            // If the slot name is not mentioned in the question it will throw an error.
            if (!slotNamesInQuestion.contains(slotName)) {
                throw new IllegalArgumentException("The provided slot does not appear in the question." +
                        "\nThe slot's name: " + slotName +
                        "\nThe available slots: " + slotNamesInQuestion.toString());
            }
            Slot slot = new Slot(slotName);
            slotNames.add(slotName);

            // Adds all the units to the slot
            NodeList slotUnitList = ((Element) slotNode).getElementsByTagName("SlotUnit");
            for (int k = 0; k < slotUnitList.getLength(); k++) {
                Node slotUnitNode = slotUnitList.item(k);
                new SlotUnit(getAttributeTextFromNode(slotUnitNode, "unitText"), slot);
            }
            slots.add(slot);
        }
        return slots;
    }

    /**
     * Checks whether the first list of strings is completely mentioned in the second list of strings.
     * If not, it will throw an exception where the second list of strings is mentioned
     * and the string that misses from it.
     * @param slotNamesInQuestion Should be all the slot names in the question.
     *                            Can be called with {@link Question#getSlotNames()}
     * @param slotNames Should be all the names of all the slots.
     * @throws IllegalArgumentException Gets thrown when one element of the first list
     * is not mentioned in the second one. See {@link List#containsAll(Collection)}
     */
    protected void questionSlotsContainsAllWithError(List<String> slotNamesInQuestion, List<String> slotNames) throws IllegalArgumentException{
        // If a slot in the question does not appear in the slots. Throw an exception
        for (String questionSlot : slotNamesInQuestion) {
            if (!slotNames.containsAll(slotNamesInQuestion)) {
                throw new IllegalArgumentException("A slot in the question is not mentioned in the list of slots." +
                        "\nThe problematic slot in the question is: " + questionSlot +
                        "\nThe list of names of slots: " + slotNames.toString());
            }
        }
    }

    /**
     * Gets all the actions in the given element.
     * @param skillElement The element of the skill where it needs to scan for actions.
     * @param slots The slots that have been found in the same element. This is to check for errors.
     * @return The list of actions it was able to find in the skillElement.
     * @throws IllegalArgumentException Gets thrown when an action attempts to use a slot that does not exist
     * or when it attempts to use a slot unit that does not exist.
     */
    protected List<Action> getActions(Element skillElement, List<Slot> slots) throws IllegalArgumentException {
        NodeList actionsList = ((Element) skillElement.getElementsByTagName("Actions").item(0))
                .getElementsByTagName("Action");
        List<Action> actions = new LinkedList<>();
        for (int j = 0; j < actionsList.getLength(); j++) {
            Node actionNode = actionsList.item(j);

            // Get all the slot units of the action.
            List<SlotUnit> actionUnits = new LinkedList<>();
            NodeList actionUnitList = ((Element) actionNode).getElementsByTagName("ActionUnit");
            for (int k = 0; k < actionUnitList.getLength(); k++) {
                Node actionUnitNode = actionUnitList.item(k);

                // Gets the name of the slot
                String actionUnitSlotName = "";
                if (actionUnitNode.getNodeType() == Node.ELEMENT_NODE &&
                        actionUnitNode.getNodeName().equals("ActionUnit")) {
                    Element actionUnitElement = (Element) actionUnitNode;
                    Node actionUnitSlotNode = actionUnitElement.getElementsByTagName("ActionUnitSlot")
                            .item(0);
                    actionUnitSlotName = getAttributeTextFromNode(actionUnitSlotNode,
                            "actionUnitSlotName");
                }
                // Gets the corresponding slot with the name
                Slot actionUnitSlot = slotListContainsSlot(slots, actionUnitSlotName);

                if (actionUnitSlot == null) {
                    throw new IllegalArgumentException("The requested slot does not appear " +
                            "in the list of slots. " +
                            "\nThe requested slot has the name: " + actionUnitSlotName +
                            "\nThe list of slots: " + slots.toString());
                }

                // Gets the corresponding unit from the found slot with the given text.
                String actionUnitText = getAttributeTextFromNode(actionUnitNode, "actionUnitText");
                SlotUnit actionUnit = actionUnitSlot.getSlotUnit(actionUnitText);

                // If the slot unit does not exist. Then an error is thrown.
                if (actionUnit == null) {
                    throw new IllegalArgumentException("The given slot unit in the action, " +
                            "is not mentioned in the slots. " +
                            "\nThe text of the unit you're trying to find: " + actionUnitText +
                            "\nSlot units in the slot: " + actionUnitSlot.getUnits().toString());
                }
                actionUnits.add(actionUnit);
            }
            String actionText = getAttributeTextFromNode(actionNode, "actionText");

            actions.add(new Action(actionUnits, actionText));
        }
        return actions;
    }

    @Override
    public boolean removeSkillFromFile(Skill skillToRemove) {
        boolean success = false;

        Node skillNode = findNodeOfSkill(skillToRemove);
        if (skillNode != null) {
            success = true;
            this.rootNode.removeChild(skillNode);
        }
        success = success && saveChanges();
        return success;
    }

    @Override
    public boolean replaceSkillInFile(Skill oldSkill, Skill newSkill,
                                      boolean replaceQuestion, boolean replaceSlots, boolean replaceActions) {
        boolean success = false;

        Node skillNode = findNodeOfSkill(oldSkill);
        if (skillNode != null) {
            if (skillNode.getNodeType() == Node.ELEMENT_NODE && skillNode.getNodeName().equals("Skill")) {
                Element skillElement = (Element) skillNode;
                if (replaceQuestion) {
                    replaceElement(createQuestionElement(newSkill.getQuestion().toString()), skillElement,
                            "Question", skillNode);
                }
                if (replaceSlots) {
                    replaceElement(createSlotsElement(newSkill.getSlots()), skillElement,
                            "Slots", skillNode);
                }
                if (replaceActions) {
                    replaceElement(createActionsElement(newSkill.getActions()), skillElement,
                            "Actions", skillNode);
                }
                success = true;
            }
        }
        success = success && saveChanges();
        return success;
    }

    protected void replaceElement(Element newChild, Element skillElement, String tagName, Node parent) {
        Node oldChild = skillElement.getElementsByTagName(tagName).item(0);
        parent.replaceChild(newChild, oldChild);
    }

    /**
     * Saves changes to the xml
     * @return Whether the save was successful.
     */
    protected boolean saveChanges() {
        try {
            DOMSource source = new DOMSource(this.document);
            StreamResult result = new StreamResult(this.file);
            this.transformer.transform(source, result);
            return true;
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Creates a question element, based on the given string.
     * This element's tag is "Question" and the attribute with the name "questionText" is the given string.
     */
    protected Element createQuestionElement(String questionString) {
        Element question = this.document.createElement("Question");
        question.setAttribute("questionText", questionString);
        return question;
    }

    /**
     * Takes a list of slots and converts it to an element. The element's tag is "Slots".
     * Its child nodes are the slots.
     * Each slot's tag is "Slot", has a unique identifier for each skill
     * and has another attribute named "slotName" with the name of the slot.
     * Its child nodes are the slot units of the slot.
     * Each slot unit's tag is "SlotUnit", has a unique identifier for each slot
     * and has another attribute named "unitText" with the with the text of the slot unit.
     */
    protected Element createSlotsElement(List<Slot> slots) {
        Element allSlots = this.document.createElement("Slots");
        int slotId = 0;
        for (Slot slot : slots) {
            // Creating an element for a slot. It will be given a unique id per slot of the skill.
            Element slotElement = this.document.createElement("Slot");
            slotElement.setAttribute("id", Integer.toString(slotId));
            slotElement.setAttribute("slotName", slot.getName());

            // Creating the elements for the slot units of the slot. They will be added to the slot.
            int slotUnitId = 0;
            for (SlotUnit slotUnit : slot.getUnits()) {
                // Creating an element for a slot unit. It will be given a unique id per slot unit of the slot.
                Element slotUnitElement = this.document.createElement("SlotUnit");
                slotUnitElement.setAttribute("id", Integer.toString(slotUnitId));
                slotUnitElement.setAttribute("unitText", slotUnit.getSlotUnitText());

                // Adding the slot unit element to the slot.
                slotElement.appendChild(slotUnitElement);
                slotUnitId++;
            }
            // Adding the slot element to the slots.
            allSlots.appendChild(slotElement);
            slotId++;
        }
        return allSlots;
    }

    /**
     * Takes a list of actions and converts it to an element. The element's tag is "Actions".
     * Its child nodes are the actions.
     * Each action's tag is "Action", with a unique id.
     * Another attribute with the name "actionText" is the text of the action,
     * i.e. the answer the bot should output when the correct question is asked.
     * The child nodes of the action are its slot units.
     * Each slot unit of the action has the tag "ActionUnit" and has a unique id.
     * Another attribute with the name "actionUnitText" is the text of the slot unit.
     * This node has one child which indicates the slot the slot unit belongs to. This has the tag "ActionUnitSlot".
     * This node has the an attribute named "actionUnitSlotName" with the name of the slot.
     */
    protected Element createActionsElement(List<Action> actions) {
        Element allActions = this.document.createElement("Actions");
        int actionId = 0;
        for (Action action : actions) {
            // Creating an element for an action. It will be given a unique id per action of the skill.
            Element actionElement = this.document.createElement("Action");
            actionElement.setAttribute("id", Integer.toString(actionId));

            // Adds the text of the action as an attribute.
            actionElement.setAttribute("actionText", action.getActionText());

            // Creating elements for each slot unit of the action. They will be added to action.
            int actionUnitId = 0;
            for (SlotUnit unit : action.getSlotUnits()) {
                // Creating an element for the slot unit of the action.
                // Its id will be unique for each slot unit of the action.
                Element actionUnitElement = this.document.createElement("ActionUnit");
                actionUnitElement.setAttribute("id", Integer.toString(actionUnitId));

                // Adds the slot unit's text as an attribute
                actionUnitElement.setAttribute("actionUnitText", unit.getSlotUnitText());

                // Adding the name of the slot the unit belongs to as a separate element to this element.
                Element slotActionUnitElement = this.document.createElement("ActionUnitSlot");
                slotActionUnitElement.setAttribute("actionUnitSlotName", unit.getSlot().getName());
                actionUnitElement.appendChild(slotActionUnitElement);

                // Adding the slot unit of the action to the action.
                actionElement.appendChild(actionUnitElement);
                actionUnitId++;
            }
            // Adding the action element to the skill.
            allActions.appendChild(actionElement);
            actionId++;
        }
        return allActions;
    }

    /**
     * Gets the attribute value of the given node, which has the given attribute name.
     */
    protected String getAttributeTextFromNode(Node node, String attributeName) {
        NamedNodeMap nodeMap = node.getAttributes();
        Node item = nodeMap.getNamedItem(attributeName);
        return item.getTextContent();
    }

    /**
     * Loops over the node list and returns the highest id found.
     * @param nodeList Each node in this list should have the attribute with the name "id", where each id is a number.
     * @return The highest id found.
     * If the node list has no nodes with an attribute with the name "id" then -1 is returned.
     */
    protected int getHighestId(NodeList nodeList) {
        // The default value
        int highestId = -1;

        // Finds the highest id by looping through all the nodes in the given list.
        for (int i = 0; i < nodeList.getLength(); i++) {
            // The current node
            Node node = nodeList.item(i);

            // Finding the id of the current node
            NamedNodeMap nodeMap = node.getAttributes();
            Node idNode = nodeMap.getNamedItem("id");
            String idString = idNode.getTextContent();
            int id = Integer.parseInt(idString);

            // If the found id is higher than the highest id. Make the highest id, the current id.
            if (id > highestId) {
                highestId = id;
            }
        }
        return highestId;
    }

    /**
     * Finds the node that corresponds to the skill.
     */
    protected Node findNodeOfSkill(Skill skill) {
        boolean skillInMap = this.skillToId.containsKey(skill);

        // Gets all the nodes of the root. I.e. all the skills.
        // If the requested skill has been found it stops the loop by returning the node.
        NodeList skillList = ((Element) this.rootNode).getElementsByTagName("Skill");
        for (int i = 0; i < skillList.getLength(); i++) {
            Node skillNode = skillList.item(i);

            if (!skillInMap) {
                // Check if the found skill is of the right type, and whether it's name/tag is "Skill".
                // These should both normally be true.
                if (skillNode.getNodeType() == Node.ELEMENT_NODE && skillNode.getNodeName().equals("Skill")) {
                    Element skillElement = (Element) skillNode;

                    // Gets the question in the skill
                    Node questionNode = skillElement.getElementsByTagName("Question").item(0);
                    Question question = new Question(getAttributeTextFromNode(questionNode, "questionText"));

                    // Checks if the correct skill has been found.
                    if (question.equals(skill.getQuestion())) {
                        return skillNode;
                    }
                }
            } else {
                if (checkSkillID(skillNode, this.skillToId.get(skill))) {
                    return skillNode;
                }
            }
        }
        return null;
    }

    protected static boolean checkSkillID(Node skillNode, Integer integer) {
        int skillId = integer;
        NamedNodeMap namedNodeMap = skillNode.getAttributes();
        Node item = namedNodeMap.getNamedItem("id");
        String foundIdString = item.getTextContent();
        int foundId = Integer.parseInt(foundIdString);
        return foundId == skillId;
    }
}
