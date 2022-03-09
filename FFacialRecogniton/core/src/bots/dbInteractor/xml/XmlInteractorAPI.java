package bots.dbInteractor.xml;

import bots.skill.api.APIQuestion;
import bots.skill.api.APISkill;
import bots.skill.api.slot.APISlot;
import bots.skill.api.slot.slotType.SlotType;
import bots.skill.api.type.APIType;
import bots.skill.api.type.APITypeName;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class XmlInteractorAPI extends XmlInteractor {

    public XmlInteractorAPI(File file) throws IOException, SAXException, ParserConfigurationException {
        super(file);

        int highestAPIId = getHighestId(((Element) this.rootNode).getElementsByTagName("APISkill"));
        if (this.skillIdIterator < highestAPIId) {
            this.skillIdIterator = highestAPIId;
        } else if (this.skillIdIterator == highestAPIId) {
            this.skillIdIterator++;
        }
    }

    /**
     * Saves the skill in the xml file. The name of the element is "APISkill". This element has three attributes.
     * One is "id", which is unique for each skill, even the other skills.
     */
    public void saveToFile(APISkill skill) {
        // Creates the skill element and adding it to the root.
        Element newSkill = this.document.createElement("APISkill");
        this.rootNode.appendChild(newSkill);

        // Setting the id of the root. This id should be unique for each skill.
        newSkill.setAttribute("id", Integer.toString(this.skillIdIterator));

        // Adds the skill to the skillToId map together with its iterator.
        this.skillToId.put(skill, this.skillIdIterator);

        // Creating the element for the question of the skill and adding it to the skill element.
        newSkill.appendChild(createQuestionElement(skill.getQuestion().toString()));

        // Creating the elements for the slots of the skill and adding it to the skill.
        newSkill.appendChild(createAPISlotsElement(skill.getSlots()));

        // Creating the elements for the type of the skill and adding it to the skill.
        newSkill.appendChild(createAPITypeElement(skill.getType()));

        // Save the changes to the file and increasing the iterator by 1.
        if (saveChanges()) {
            this.skillIdIterator++;
        }
    }

    /**
     * Takes a list of api slots and converts it to an element. The element's tag is "Slots".
     * Its child nodes are the slots.
     * Each slot's tag is "Slot", has a unique identifier for each skill, whether it's api or template
     * and has another attribute named "slotName" with the name of the slot
     */
    protected Element createAPISlotsElement(List<APISlot> slots) {
        Element allSlots = this.document.createElement("Slots");
        int slotId = 0;
        for (APISlot slot : slots) {
            // Creating an element for a slot. It will be given a unique id per slot of the skill.
            Element slotElement = this.document.createElement("Slot");
            slotElement.setAttribute("id", Integer.toString(slotId));
            slotElement.setAttribute("slotName", slot.getName());
            slotElement.setAttribute("type", slot.getType().getName().name());

            // Adding the slot element to the slots.
            allSlots.appendChild(slotElement);
            slotId++;
        }
        return allSlots;
    }

    /**
     * Creates the element for the api type. The element has the tagname "APIType". And has two attributes.
     * One is the type name, with the name "typeName". The other one has the name "key" and contains the api key
     * @return The created element.
     */
    protected Element createAPITypeElement(APIType type) {
        Element typeElement = this.document.createElement("APIType");
        typeElement.setAttribute("typeName", type.getName().name());
        typeElement.setAttribute("key", type.getKey());
        return typeElement;
    }

    public List<Object> convertFileToBothSkills() {
        List<Object> bothSkills = new LinkedList<>();
        bothSkills.addAll(convertFileToSkills());
        bothSkills.addAll(convertFileToAPISkills());
        return bothSkills;
    }

    public List<APISkill> convertFileToAPISkills() {
        List<APISkill> skills = new LinkedList<>();

        // Gets all the nodes of the root. I.e. all the skills
        NodeList skillList = this.rootNode.getChildNodes();
        for (int i = 0; i < skillList.getLength(); i++) {
            Node skillNode = skillList.item(i);

            // Check if the found skill is of the right type, and whether it's name/tag is "Skill".
            // These should both normally be true.
            if (skillNode.getNodeType() == Node.ELEMENT_NODE && skillNode.getNodeName().equals("APISkill")) {
                Element skillElement = (Element) skillNode;

                // Gets the api type in the skill
                Node apiTypeNode = skillElement.getElementsByTagName("APIType").item(0);
                String apiTypeNameString = getAttributeTextFromNode(apiTypeNode, "typeName");
                String key = getAttributeTextFromNode(apiTypeNode, "key");
                APIType type = APIType.getInstance(apiTypeNameString, key);

                // Gets the question in the skill
                Node questionNode = skillElement.getElementsByTagName("Question").item(0);
                APIQuestion question = new APIQuestion(getAttributeTextFromNode(questionNode,
                        "questionText"));

                // Gets all the slots in the skill
                List<String> slotNamesInQuestion = question.getSlotNames();
                List<String> slotNames = new LinkedList<>();
                List<APISlot> slots = getAPISlots(skillElement, slotNamesInQuestion, slotNames);

                // Exception checking
                questionSlotsContainsAllWithError(slotNamesInQuestion, slotNames);

                // Puts the found skill in the skillToId map.
                APISkill skill = new APISkill(question, slots, type);
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
    protected List<APISlot> getAPISlots(Element skillElement, List<String> slotNamesInQuestion, List<String> slotNames) throws IllegalArgumentException {
        NodeList slotsList = ((Element) skillElement.getElementsByTagName("Slots").item(0))
                .getElementsByTagName("Slot");
        List<APISlot> slots = new LinkedList<>();
        for (int j = 0; j < slotsList.getLength(); j++) {
            Node slotNode = slotsList.item(j);

            // Gets the name of the slot and the corresponding api key.
            String slotName = getAttributeTextFromNode(slotNode, "slotName");
            SlotType slotType = SlotType.getInstance(getAttributeTextFromNode(slotNode, "type"));

            // If the slot name is not mentioned in the question it will throw an error.
            if (!slotNamesInQuestion.contains(slotName)) {
                throw new IllegalArgumentException("The provided slot does not appear in the question." +
                        "\nThe slot's name: " + slotName +
                        "\nThe available slots: " + slotNamesInQuestion.toString());
            }
            APISlot slot = new APISlot(slotName, slotType);
            slotNames.add(slotName);
            slots.add(slot);
        }
        return slots;
    }

    public boolean removeSkillFromFile(APISkill skillToRemove) {
        boolean success = false;

        Node skillNode = findNodeOfSkill(skillToRemove);
        if (skillNode != null) {
            success = true;
            this.rootNode.removeChild(skillNode);
        }
        success = success && saveChanges();
        return success;
    }

    /**
     * Finds the node that corresponds to the skill.
     */
    protected Node findNodeOfSkill(APISkill skill) {
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
                    APIQuestion question = new APIQuestion(getAttributeTextFromNode(questionNode, "questionText"));

                    // Checks if the correct skill has been found.
                    if (question.equals(skill.getQuestion())) {
                        return skillNode;
                    }
                }
            } else {
                if (XmlInteractor.checkSkillID(skillNode, this.skillToId.get(skill))) {
                    return skillNode;
                }
            }
        }
        return null;
    }

    public boolean replaceSkillInFile(APISkill oldSkill, APISkill newSkill,
                                      boolean replaceQuestion, boolean replaceSlots) {
        boolean success = false;

        Node skillNode = findNodeOfSkill(oldSkill);
        if (skillNode != null) {
            if (skillNode.getNodeType() == Node.ELEMENT_NODE && skillNode.getNodeName().equals("APISkill")) {
                Element skillElement = (Element) skillNode;
                if (replaceQuestion) {
                    replaceElement(createQuestionElement(newSkill.getQuestion().toString()), skillElement,
                            "Question", skillNode);
                }
                if (replaceSlots) {
                    replaceElement(createAPISlotsElement(newSkill.getSlots()), skillElement,
                            "Slots", skillNode);
                }
                success = true;
            }
        }
        success = success && saveChanges();
        return success;
    }
}
