package de.tum.whatsappplus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MessageSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private String[] selectedContacts;
    private String groupTitle;
    private ArrayList<Contact> contacts;
    private Spinner contactSpinner;
    private TableLayout messageList;

    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_selection);
        selectedContacts = getIntent().getStringArrayExtra(Constants.EXTRA_CONTACTS_ID);
        groupTitle = getIntent().getStringExtra("groupTitle");
        messageList = (TableLayout) findViewById(R.id.messageList);

        contactSpinner = (Spinner) findViewById(R.id.contactSpinner);
        ArrayAdapter<String > adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selectedContacts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactSpinner.setAdapter(adapter);
        contactSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                messageList.removeAllViews();
                String contact = (String) ((TextView) selectedItemView).getText();
                List<Message> messages = Constants.contacts.get(contact).chat;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                return;
            }

        });


        contacts = new ArrayList<>();
        for(String key : Constants.contacts.keySet()) {
            Contact c = Constants.contacts.get(key);
            for(String selected : selectedContacts) {
                if(c.name.equals(selected)) {
                    contacts.add(c);
                }
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New group");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void onDoneClick(View view) {

    }

    private void addNewChatMessage(Message message) {
        View chatItem = getLayoutInflater().inflate(R.layout.view_chat_item, table, false);
        ((TextView) chatItem.findViewById(R.id.chat_message)).setText(message.text);
        ((TextView) chatItem.findViewById(R.id.chat_timestamp)).setText(message.timeStamp);

        View chatMessageContent = chatItem.findViewById(R.id.chat_message_content);

        TableLayout.LayoutParams chatItemLayoutParams = (TableLayout.LayoutParams) chatItem.getLayoutParams();
        chatItemLayoutParams.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.MESSAGE_MARGIN_TOP, getResources().getDisplayMetrics());

        LinearLayout.LayoutParams chatMessageContentLayoutParams = (LinearLayout.LayoutParams) chatMessageContent.getLayoutParams();

        if (message.author.equals("self")) {
            chatMessageContentLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            chatMessageContentLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            chatMessageContent.setBackground(getResources().getDrawable(R.drawable.drawable_chat_item_background_self));
        } else {
            chatMessageContentLayoutParams.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            chatMessageContentLayoutParams.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
            chatMessageContent.setBackground(getResources().getDrawable(R.drawable.drawable_chat_item_background_other));
        }

        chatItem.setTag(R.string.tag_chat_message, message);
        chatMessageContent.setOnClickListener(this);

        chatMessageContent.setLayoutParams(chatMessageContentLayoutParams);

        table.addView(chatItem, chatItemLayoutParams);
    }

    @Override
    public void onClick(View v) {
        selectOrDeselectMessage(v);
    }

    private void selectOrDeselectMessage(View messageContent) {
        View chatItem = (View) messageContent.getParent();
        Message message = (Message) chatItem.getTag(R.string.tag_chat_message);
        boolean chatItemSelected = message.selected;
        if ("self".equals(message.author)) {
            selectSelfMessage(!chatItemSelected, messageContent, chatItem);
        } else {
            selectOtherMessage(!chatItemSelected, messageContent, chatItem);
        }

        message.selected = !message.selected;
    }

    private void selectSelfMessage(boolean toggle, View messageContent, View chatItem) {
        if (toggle) {
            messageContent.setBackground(getResources().getDrawable(R.drawable.drawable_chat_item_background_self_sel));
            chatItem.setBackgroundColor(getResources().getColor(R.color.color_chat_item_background_sel));
        } else {
            messageContent.setBackground(getResources().getDrawable(R.drawable.drawable_chat_item_background_self));
            chatItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }

    private void selectOtherMessage(boolean toggle, View messageContent, View chatItem) {
        if (toggle) {
            messageContent.setBackground(getResources().getDrawable(R.drawable.drawable_chat_item_background_other_sel));
            chatItem.setBackgroundColor(getResources().getColor(R.color.color_chat_item_background_sel));
        } else {
            messageContent.setBackground(getResources().getDrawable(R.drawable.drawable_chat_item_background_other));
            chatItem.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }
}
