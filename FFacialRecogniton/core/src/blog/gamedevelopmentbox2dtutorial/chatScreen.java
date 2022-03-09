package blog.gamedevelopmentbox2dtutorial;

import bots.skill.template.Skill;
import bots.skill.template.question.Question;
import bots.skill.template.question.QuestionWord;
import bots.skill.template.slot.Slot;
import bots.skill.template.Action;
import bots.skill.template.slot.SlotUnit;
import bots.TemplateBot;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import javax.swing.*;
import java.util.*;
import java.util.List;

/**
 * chat window & skills window
 * @author Isabel
 */
public class chatScreen extends JFrame implements Screen {

    public Stage stage;
    private Box2DTutorial parent;
    Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

    public String username = loginScreen.getUsername();
    public String helper = " Billy: ";

    public chatScreen(Box2DTutorial box2dTutorial) {//constructer
        parent = box2dTutorial;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
    }


    private Table editTable, deleteTable, skillTable;
    // chat room table actors
    private Table chatTable;
    private TextButton send_button, skill_button;
    private Label chat_label;
    private TextArea message_field, skill_field;
    private ScrollPane input_scroll, chat_scroll;

    // create skill table actors
    private Table choiceTable;
    private Label question_label, slot_label, answer_label, endquestion_label, fill_label;
    private TextArea skill_question, skill_slot, skill_answer;
    private TextButton question_button, slot_button, answer_button, done_button, create_button, another_button, addUnit_button;
    private Question question;
    private Slot parentslot;
    private SlotUnit unit1Slot;
    private LinkedList<SlotUnit> unitsList = new LinkedList<>();
    private List<SlotUnit> actionUnits = new LinkedList<>();
    private LinkedList<Action> actions = new LinkedList<>();
    private LinkedList<Slot> allslots = new LinkedList<>();
    private List<Slot> slotsNotInSelectBox = new LinkedList<>();
    private Array<String> slotarray = new Array<>();
    private Map<String, Action> stringActionMap = new HashMap<>();
    private TemplateBot bot1;

    //create choice skill table actors
    private TextButton deleting_button, editing_button;

    //edit skill table actors
    private TextArea editSkill_question, editSkill_slot, editSkill_answer;
    private Label selectQuestion_label, editQuestion_label, editSlot_label, editAnswer_label;
    private TextButton editQuestion_button, editSlot_button, editAnswer_button, editDone_button, chooseskill_button;
    private Array<String> questionNowArray = new Array<>();
    private Array<String> newSlotArray = new Array<>();
    private Question editQuestion;
    private LinkedList<Slot> editAllslots = new LinkedList<>();
    private LinkedList<Action> editActions = new LinkedList<>();
    private int t;
    private Slot editParentslot;
    private SlotUnit editUnit1Slot;
    private Skill editedSkill;
    private Skill skillToEdit;

    //delete skill table actors
    private TextButton delete_button, backedit_button, deleteall_button;
    private Label delete_label;
    private Array<String> questionArray = new Array<>();

    @Override
    public void show() {
        bot1 = new TemplateBot();

        //create chat table
        chatTable = new Table();
        chatTable.setFillParent(true);
        stage.addActor(chatTable);

        //create field where conversation is shown
        chat_label = new Label(helper +"Hello " + username + "! How can I help you?", skin);
        chat_label.setWrap(true);
        chat_label.setAlignment(Align.topLeft);
        //enable user to scroll down in this field
        chat_scroll = new ScrollPane(chat_label, skin);
        chat_scroll.setFadeScrollBars(false);
        chatTable.add(chat_scroll).width(500f).height(400f).colspan(3);

        //typing area for user
        message_field = new TextArea("type your question here", skin);
        message_field.setPrefRows(2);
        message_field.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                message_field.setText("");
            }
        });
        input_scroll = new ScrollPane(message_field, skin);
        input_scroll.setFadeScrollBars(false);
        chatTable.row();
        chatTable.add(input_scroll).width(300f);


        //create send button
        send_button = new TextButton("send", skin);
        //sends user input to the bot to get the correct response shown in the chat box
        send_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String text = message_field.getText();
                bot1.ask(text);
                if(!text.isEmpty()){
                    StringBuilder old = chat_label.getText();
                    chat_label.setText(old + "\n"  +" "+username +": " + text + "\n" + helper +bot1.ask(text));
                    message_field.setText("");
                }

            }
        });
        chatTable.add(send_button).padRight(9).padLeft(4);
        //create skill button
        skill_button = new TextButton("create/delete skill", skin);
        chatTable.add(skill_button);
        chatTable.setVisible(true);



        //CREATE CHOICE TABLE
        choiceTable = new Table();
        choiceTable.setFillParent(true);
        stage.addActor(choiceTable);
        deleting_button = new TextButton(" delete skill ", skin);
        editing_button = new TextButton("edit skill", skin);
        create_button = new TextButton(" create skill ", skin);
        choiceTable.add(editing_button).padRight(30);
        choiceTable.add(deleting_button).padRight(20);
        choiceTable.add(create_button).padLeft(20);
        choiceTable.setVisible(false);


        //CREATE EDIT TABLE
        editTable = new Table();
        editTable.setFillParent(true);
        stage.addActor(editTable);
        editTable.setVisible(false);
        //create elements for in skill table
        editSkill_question = new TextArea("", skin);
        editSkill_slot = new TextArea("", skin);
        editSkill_answer = new TextArea("", skin);
        editSkill_question.setPrefRows(1);
        editSkill_slot.setPrefRows(2);
        editSkill_answer.setPrefRows(2);
        //create labels
        selectQuestion_label = new Label("select skill: ", skin);
        editQuestion_label = new Label("question: ", skin);
        editSlot_label = new Label("slot options: ", skin);
        editAnswer_label = new Label("actions: ", skin);
        //create buttons
        editQuestion_button = new TextButton("change", skin);
        editSlot_button = new TextButton("change", skin);
        editAnswer_button = new TextButton("change", skin);
        editDone_button = new TextButton("done and go back", skin);
        chooseskill_button = new TextButton("choose", skin);
        //another_button = new TextButton("edit another skill", skin);
        //create dropdown menu
        final SelectBox<String> skillSelector = new SelectBox<>(skin);
        final SelectBox<String> slotSelector = new SelectBox<>(skin);
        final SelectBox<String> slotUnitSelector = new SelectBox<>(skin);
        final SelectBox<String> actionSelector = new SelectBox<>(skin);

        //adding elements to table
        editTable.add(selectQuestion_label);
        editTable.add(skillSelector);
        editTable.add(fill_label);
        editTable.add(fill_label);
        editTable.add(chooseskill_button).padLeft(15);
        editTable.row();
        editTable.add(fill_label);
        editTable.add(fill_label);
        editTable.add(fill_label);
        editTable.row();
        editTable.add(editQuestion_label);
        editTable.add(editSkill_question).width(200f);
        editTable.add(fill_label);
        editTable.add(fill_label);
        editTable.add(editQuestion_button).padLeft(10);
        editTable.row();
        editTable.row();
        editTable.add(editSlot_label);
        editTable.add(slotSelector);
        editTable.add(slotUnitSelector);
        editTable.add(editSkill_slot).width(200f);
        editTable.add(editSlot_button).padLeft(10);
        editTable.row();
        editTable.row();
        editTable.add(editAnswer_label);
        editTable.add(actionSelector);
        editTable.add(fill_label);
        editTable.add(editSkill_answer).width(200f);
        editTable.add(editAnswer_button).padLeft(10);
        editTable.row();
        editTable.add(fill_label);
        editTable.row();
        editTable.add(fill_label);
        editTable.row();
        editTable.add(fill_label);
        editTable.add(editDone_button).padTop(20);


        /* Big to do's list.
        *
        * TODO 4: When action changes
        *  TODO 4.2: Find if action was added
        *   TODO 4.2.1: Get action
        *   TODO 4.2.2: Add action
        *
        * End of to do's list.
         */

        editing_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //change screen to editScreen
                updateSkillSelector(skillSelector);
                editTable.setVisible(true);
                choiceTable.setVisible(false);
            }
        });
        editDone_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // change editScreen to chatScreen
                if (skillToEdit != null || editedSkill != null) {
                    bot1.editSkill(skillToEdit, editedSkill.getQuestion(),
                            editedSkill.getSlots(), editedSkill.getActions());
                }
                editTable.setVisible(false);
                chatTable.setVisible(true);
                skillToEdit = null;
                editedSkill = null;
            }
        });
        chooseskill_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //set textfields to the selected skill values
                Question tempQ = new Question(skillSelector.getSelected());
                for (Skill skill : bot1.getSkills()) {
                    if (tempQ.equals(skill.getQuestion())) {
                        skillToEdit = skill;
                        editedSkill = new Skill(skill);
                        updateActionSelector(actionSelector);
                        updateSlotsSelector(slotSelector);
                        updateUnitSelector(slotUnitSelector, editedSkill.getSlot(slotSelector.getSelected()));
                        editSkill_question.setText(editedSkill.getQuestion().toString());
                        SlotUnit unit = editedSkill.getSlot(slotSelector.getSelected())
                                .getSlotUnit(slotUnitSelector.getSelected());
                        editSkill_slot.setText(unit.getSlotUnitText());
                        editSkill_answer.setText(stringActionMap.get(actionSelector.getSelected()).getActionText());
                        break;
                    }
                }
            }
        });
        editQuestion_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editedSkill.setQuestion(new Question(editSkill_question.getText()));
                updateSlotsSelector(slotSelector);
                updateUnitSelector(slotUnitSelector, editedSkill.getSlot(slotSelector.getSelected()));
            }
        });
        editSlot_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Slot slot = editedSkill.getSlot(slotSelector.getSelected());
                String unitText = slotUnitSelector.getSelected();
                SlotUnit unit = slot.getSlotUnit(unitText);
                if (unit != null) {
                    unit.setSlotUnitText(editSkill_slot.getText());
                } else if (unitText.equals("NEW_UNIT")) {
                    new SlotUnit(editSkill_slot.getText(), slot);
                }
                updateActionSelector(actionSelector);
                updateUnitSelector(slotUnitSelector, slot);
            }
        });

        editAnswer_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String actionDesc = actionSelector.getSelected();
                if (stringActionMap.containsKey(actionDesc)) {
                    for (Action action : editedSkill.getActions()) {
                        if (action.equals(stringActionMap.get(actionDesc))) {
                            action.setAction(editSkill_answer.getText());
                        }
                    }
                } else {

                }
            }
        });
        slotSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String slotName = slotSelector.getSelected();
                Slot slot = editedSkill.getSlot(slotName);
                Array<String> unitList = new Array<>();
                if (slot == null) {
                    slot = new Slot(slotName);
                    editedSkill.addSlot(slot);
                }
                for (SlotUnit unit : slot.getUnits()) {
                    unitList.add(unit.getSlotUnitText());
                }
                slotUnitSelector.setItems(unitList);
                updateUnitSelector(slotUnitSelector, slot);
            }
        });
        actionSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String actionDesc = actionSelector.getSelected();
                if (stringActionMap.containsKey(actionDesc)) {
                    editSkill_answer.setText(stringActionMap.get(actionDesc).getActionText());
                } else {
                    editSkill_answer.setText("NEW_ACTION");
                }
            }
        });
        slotUnitSelector.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                editSkill_slot.setText(slotUnitSelector.getSelected());
            }
        });


        //CREATE DELETE TABLE
        deleteTable = new Table();
        deleteTable.setFillParent(true);
        stage.addActor(deleteTable);

        delete_label = new Label("select unwanted skill: ",skin);
        fill_label = new Label("",skin);
        delete_button = new TextButton(" delete", skin);
        deleteall_button = new TextButton(" delete all skills", skin);
        //create dropdown menu
        final SelectBox<String> selectBox3 = new SelectBox<>(skin);
        backedit_button = new TextButton("back to chat",skin);


        deleteTable.add(delete_label).padRight(20);
        deleteTable.add(selectBox3);
        deleteTable.add(delete_button).padLeft(20);
        deleteTable.row();
        deleteTable.add(fill_label);
        deleteTable.row();
        deleteTable.add(deleteall_button).padTop(30).padLeft(13);
        deleteTable.add(fill_label);
        deleteTable.add(backedit_button).padTop(30).padLeft(20);


        choiceTable.setVisible(false);

        delete_button.addListener(new ChangeListener() {
            Array<String> deleteQuestionArray(int i){
                bot1.removeSkill(bot1.getSkills().get(i));
                questionArray.removeIndex(i);
                return questionArray;
            }
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for(int j=0;j<questionArray.size;j++) {
                    if (selectBox3.getSelected().equals(String.valueOf(questionArray.get(j)))) {
                        selectBox3.setItems(deleteQuestionArray(j));
                    }
                }
            }
        });

        backedit_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //change screen to chatScreen
                chatTable.setVisible(true);
                deleteTable.setVisible(false);
            }
        });
        deleteall_button.addListener(new ChangeListener() {
            Array<String> deleteallArray(){
                bot1.removeAllSkills();
                questionArray.removeRange(0,questionArray.size-1);
                return questionArray;
            }
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                selectBox3.setItems(deleteallArray());
            }
        });
        deleting_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //change screen to deleteScreen
                updateSkillSelector(selectBox3);
                deleteTable.setVisible(true);
                choiceTable.setVisible(false);
            }
        });
        deleteTable.setVisible(false);




        //CREATE SKILL TABLE
        skillTable = new Table();
        skillTable.setFillParent(true);
        stage.addActor(skillTable);

        //create elements for in skill table
        skill_question = new TextArea("How is <COUNTRY>?", skin);
        skill_slot = new TextArea("japan", skin);
        skill_answer = new TextArea("pretty", skin);
        skill_question.setPrefRows(1);
        skill_slot.setPrefRows(1);
        skill_answer.setPrefRows(1);
        //create labels
        endquestion_label = new Label("", skin);
        question_label = new Label("question: ", skin);
        slot_label = new Label("slots: ", skin);
        answer_label = new Label("answer: ", skin);
        //create buttons
        addUnit_button = new TextButton("add unit", skin);
        question_button = new TextButton("add", skin);
        slot_button = new TextButton("add", skin);
        answer_button = new TextButton("add", skin);
        done_button = new TextButton("create", skin);
        another_button = new TextButton("+ skill", skin);
        //create dropdown menu
        final SelectBox<String> selectBox1 = new SelectBox<>(skin);
        final SelectBox<String> selectBox2 = new SelectBox<>(skin);

        //adding elements to table
        skillTable.add(fill_label);
        skillTable.add(endquestion_label);
        skillTable.row();
        skillTable.add(fill_label);
        skillTable.row();
        skillTable.add(fill_label);
        skillTable.row();
        endquestion_label.setVisible(false);
        skillTable.add(question_label);
        skillTable.add(skill_question).width(260f).padLeft(10);
        skillTable.add(fill_label);
        skillTable.add(fill_label);
        skillTable.add(question_button).padLeft(10);
        skillTable.row();
        skillTable.row();
        skillTable.add(slot_label);
        skillTable.add(selectBox1);
        skillTable.add(fill_label);
        skillTable.add(skill_slot).width(100f);
        skillTable.add(slot_button).padLeft(10);
        skillTable.row();
        skillTable.row();
        skillTable.add(answer_label);
        skillTable.add(selectBox2);
        skillTable.add(addUnit_button).padRight(10);
        skillTable.add(skill_answer).width(100f);
        skillTable.add(answer_button).padLeft(10);
        skillTable.row();
        skillTable.add(fill_label);
        skillTable.row();
        skillTable.add(fill_label);
        skillTable.row();
        skillTable.add(another_button).padTop(20);
        skillTable.add(fill_label);
        skillTable.add(done_button).padTop(20);


        //so that the textfield becomes empty when clicked on by user
        skill_question.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                skill_question.setText("");
            }
        });
        skill_slot.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                skill_slot.setText("");
            }
        });
        skill_answer.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                skill_answer.setText("");
            }
        });


        question_button.addListener(new ChangeListener() {
            Array<String> questionArray(Question a){
                questionArray.add(String.valueOf(a));
                return questionArray;
            }
            Array<String> questionNowArray(){
                return questionArray;
            }
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                skill_question.setVisible(false);
                question_button.setVisible(false);
                endquestion_label.setText(skill_question.getText());
                endquestion_label.setVisible(true);
                //create question from user input
                question = new Question(skill_question.getText());
                selectBox3.setItems(questionArray(question));
                skillSelector.setItems(questionNowArray());

                Array<String> slotNamesInQuestion = new Array<>();
                for (String slotName : question.getSlotNames()) {
                    slotNamesInQuestion.add(slotName);
                }
                selectBox1.setItems(slotNamesInQuestion);

                skill_question.setText("");
            }
        });


        slot_button.addListener(new ChangeListener() {
            Array<String> slotarray() {
                //add unit to parent slot
                SlotUnit unit1Slot = new SlotUnit(skill_slot.getText(), parentslot);
                unitsList.add(unit1Slot);
                slotarray.add(String.valueOf(unit1Slot));
                return slotarray;
            }
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //the question is now set, the user can't change it anymore
                question_label.setVisible(false);

                //parent slot selected
                parentslot = new Slot(selectBox1.getSelected());

                // Ensures that there are no slots that are exactly the same in name.
                int indexOfSlot = allslots.indexOf(parentslot);
                if (indexOfSlot != -1) {
                    parentslot = allslots.get(indexOfSlot);
                } else {
                    allslots.add(parentslot);
                }
                //give options to user for next question in drop out menu (and add unit to selected parent slot)
                selectBox2.setItems(slotarray());
                skill_slot.setText("option");
            }
        });

        addUnit_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for (Slot slot : allslots) {
                    // Find correct unit and add it to the action units.
                    String selected = selectBox2.getSelected();
                    int beginIndex = selected.indexOf('>') + 2;
                    String unitText = selected.substring(beginIndex);
                    SlotUnit unit = slot.getSlotUnit(unitText);
                    if (unit != null) {
                        actionUnits.add(unit);

                        // Add slot to the list of all slots that shouldn't be displayed in the list of units
                        slotsNotInSelectBox.add(slot);
                    }
                }
                updateActionUnitMenu(selectBox2);
            }
        });

        answer_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //don't let the user add more slot options at this point
                skill_slot.setVisible(false);
                selectBox1.setVisible(false);
                slot_button.setVisible(false);
                slot_label.setVisible(false);
                actions.add(new Action(new LinkedList<>(actionUnits), skill_answer.getText()));
                skill_answer.setText("answer");
                actionUnits.clear();
                slotsNotInSelectBox.clear();
                updateActionUnitMenu(selectBox2);
            }
        });


        //creates skill and redirects user to the chat screen
        done_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addSkill(selectBox1, selectBox2);
                skillTable.setVisible(false);
                chatTable.setVisible(true);
            }
        });

        //same as done_button but stays on skill screen
        another_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                addSkill(selectBox1, selectBox2);
            }
        });

        //button on chat screen, to redirect to the choice screen
        skill_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //change screen to skillScreen
                choiceTable.setVisible(true);
                chatTable.setVisible(false);
            }
        });

        //button on choice screen, to redirect to the create skill screen
        create_button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //change screen to skillScreen
                skillTable.setVisible(true);
                choiceTable.setVisible(false);
            }
        });


        skillTable.setVisible(false);
    }

    private void updateSkillSelector(SelectBox<String> skillSelector) {
        Array<String> allSkills = new Array<>();
        for (Skill skill : bot1.getSkills()) {
            allSkills.add(skill.getQuestion().toString());
        }
        skillSelector.setItems(allSkills);
    }

    private void updateActionSelector(SelectBox<String> actionSelector) {
        stringActionMap.clear();
        List<List<SlotUnit>> usedLists = new LinkedList<>();
        Array<String> allActionUnits = new Array<>();
        for (Action action : editedSkill.getActions()) {
            List<String> actionUnits = new ArrayList<>();
            List<SlotUnit> unitsInAction = action.getSlotUnits();
            usedLists.add(unitsInAction);
            for (SlotUnit slotUnit : unitsInAction) {
                actionUnits.add(slotUnit.toString());
            }
            allActionUnits.add(actionUnits.toString());
            stringActionMap.put(actionUnits.toString(), action);
        }
        /*List<List<SlotUnit>> allCombos = Skill.getAllCombosOfAllUnits(editedSkill.getSlots());
        for (List<SlotUnit> comboSet : allCombos) {
            if (!usedLists.contains(comboSet)) {
                allActionUnits.add(comboSet.toString());
            }
        }*/

        actionSelector.setItems(allActionUnits);
    }

    private void updateSlotsSelector(SelectBox<String> slotSelector) {
        Array<String> allSlots = new Array<>();
        for (QuestionWord slot : editedSkill.getQuestion().getSlots()) {
            allSlots.add(slot.getText());
        }
        slotSelector.setItems(allSlots);
    }

    private void updateUnitSelector(SelectBox<String> unitSelector, Slot slot) {
        Array<String> allUnits = new Array<>();
        for (SlotUnit unit : slot.getUnits()) {
            allUnits.add(unit.getSlotUnitText());
        }
        allUnits.add("NEW_UNIT");
        unitSelector.setItems(allUnits);
    }

    private void updateActionUnitMenu(SelectBox<String> selectBox2) {
        Array<String> unitsToDisplay = new Array<>();
        for (Slot slot : allslots) {
            if (!slotsNotInSelectBox.contains(slot)) {
                for (SlotUnit unit : slot.getUnits()) {
                    unitsToDisplay.add(unit.toString());
                }
            }
        }
        selectBox2.setItems(unitsToDisplay);
    }

    private void addSkill(SelectBox<String> selectBox1, SelectBox<String> selectBox2) {
        bot1.createNewSkill(question, allslots, actions);
        //make question options visible again
        skill_question.setVisible(true);
        skill_question.setText("question with <SLOT>");
        endquestion_label.setText("");
        question_label.setVisible(true);
        question_label.setText("Question: ");
        endquestion_label.setVisible(true);
        question_button.setVisible(true);
        //make slot options visible again
        skill_slot.setVisible(true);
        selectBox1.setVisible(true);
        slot_button.setVisible(true);
        slot_label.setVisible(true);
        slotarray.clear();
        selectBox1.setItems("");
        selectBox2.setItems("");
        allslots.clear();
        actions.clear();
    }


    @Override
    public void render(float delta) {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}