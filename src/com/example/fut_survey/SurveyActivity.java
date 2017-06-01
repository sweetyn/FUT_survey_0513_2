package com.example.fut_survey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;


public class SurveyActivity extends Activity {
	public static final String NEW_SURVEY = "com.example.fut_survey..NEW_GAME";
	private static final int FILE_OPEN_FAIED = 2;
	private static final int BACK_TO_MAIN =3;
	private int mStep = 0;

	private LinkedList<FUTSurveyInfo> mFUTSurveyInfoList = null;
	private EditText et_comment;
	private TextView tv_question;
	private RadioGroup rb_group;
	private RadioButton rb1;
	private RadioButton rb2;
	private RadioButton rb3;
	private RadioButton rb4;
	private RadioButton rb5;
	private int numberOfQuestions;
	private int tempCheckedRBId;
	public Boolean mNewSurvey = false;
	
	private Dialog dialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_survey);
		if (getIntent().getExtras() != null
				&& getIntent().getExtras().getBoolean(NEW_SURVEY)) {
			mNewSurvey = true;
		}
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		//StepSurvey step = new StepSurvey(1);	
		
		
		
		if(loadFUTSurveyInfo() == true){
			//Determine which step is current step.
			//Hard code to 0
			//Get current step if saved before. Otherwise current step will be 0.
			if(mNewSurvey == true){
				mStep = 0;
			}else{
				SharedPreferences prefs = getPreferences(0);
				mStep =  prefs.getInt("CurrentStep", 0);
			}
			
			
			//Load UI Layout
			LoadLayout();
			
			//Load the question
			if (LoadQuestion() == -1){
				//Error occurs
			}
			
			//At this time, it is no longer new survey.
			mNewSurvey = false;
			
		}else{
			Log.e("SurveyActivity.java", "Cannot load Survey Info from file");
			LoadErrorDialog("1001");
		}
		
		
		

		//RadioButton Group Handler
		//RadioButtonHandler();
		
		//Next Button Display and Handler
		//NextButtonCreate();
		
		
		//Back Button Display and handler
		//BackButtonCreate();
	}
	
	
	private void CheckRBbutton(){	
		//int tmpRB = 0;
		int tmpRB = Integer.parseInt(mFUTSurveyInfoList.get(mStep).GetAnswer());
		if(tmpRB == 1){
			rb1.setChecked(true);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
		}else if(tmpRB == 2){
			rb1.setChecked(false);
			rb2.setChecked(true);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(false);
		}else if(tmpRB == 3){
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb3.setChecked(true);
			rb4.setChecked(false);
			rb5.setChecked(false);
		}else if(tmpRB == 4){
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(true);
			rb5.setChecked(false);
		}else if(tmpRB == 5){
			rb1.setChecked(false);
			rb2.setChecked(false);
			rb3.setChecked(false);
			rb4.setChecked(false);
			rb5.setChecked(true);
		}
	}
	
	// Create a survey info class for LinkedList
	private class FUTSurveyInfo{
		private String mQuestion = null;
		private String mAnswer = null;
		private String mComment = null;
		
		// ?? can I add void return type here? how about GameInfo? where is return type?
		 FUTSurveyInfo(String question, String answer, String comment) {
			mQuestion = question;
			mAnswer = answer;
		    mComment = comment;
		} 
		 
		 public String GetQuestion(){
			 return mQuestion;
		 }
		 
		 public void SetAnswer(String p_answer){
			 mAnswer = p_answer;
		 }
		 
		 public String GetAnswer(){
			 return mAnswer;
		 }
		 
		 public void SetComment(String p_comment){
			 mComment = p_comment;
		 }
		 
		 public String GetComment(){
			 return mComment;
		 }
	} //end of FUTSurveyInfo class
	
		
	private boolean loadFUTSurveyInfo() {
				
		// read from questionnaire text file
		String fileContents = null;

		try {
		
			File FUTSurveyInfoText = new File (Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey/d850fut_questionnaire.txt");

			InputStream in = new FileInputStream (FUTSurveyInfoText);
			//InputStream in = getResources().openRawResource(R.raw.d850fut_questionnaire);
			BufferedReader reader = null;
		
			
			
			if (in != null) {
				reader = new BufferedReader(new InputStreamReader(in));
			}
		
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("#"))
					continue;
				if (line.startsWith("*")) {
					continue;
				}
				buffer.append(line);
				buffer.append("\n");
			}
			
			if (buffer.length() == 0){
				//File opened but no valid contents
				return false;
			}
			
			reader.close();
			in.close();
			fileContents = buffer.toString();
			} catch (IOException e) {
				Log.e("SpotDiffGame", "exception in reading game data file");
				return false;
			}
		
		// Split the text file content by line
		String[] lineArray = fileContents.split("\n");
		
		StringBuffer question = new StringBuffer();
		StringBuffer answer = new StringBuffer();
		StringBuffer comment = new StringBuffer();
		
		//File Corruption Check
		if((lineArray.length % 3) != 0){
			//File opened, valid contents, but q/a/c set is not complete.  Contents corrupted
			return false;
		}
		
		// Group by question, answer, and comment
		for (int i = 0; i < lineArray.length; i += 3) {
			question.append(lineArray[0 + i]).append("\n");
			answer.append(lineArray[1 + i]).append("\n");
			comment.append(lineArray[2 + i]).append("\n");
			}
		
		
		////////////////////Safe Zone /////////////////////
		// Convert from buffer to string to split by "\n"
		String questionContents = question.toString();
		String answerContents = answer.toString();
		String commentContents = comment.toString();
		
		String[] questionArray = questionContents.split("\n");
		String[] answerArray = answerContents.split("\n");
		String[] commentArray = commentContents.split("\n");
		
		mFUTSurveyInfoList = new LinkedList<FUTSurveyInfo>();
		numberOfQuestions = lineArray.length/3;
		
		// lineArray.length/3 = number of questions
		for (int j = 0; j < numberOfQuestions ; j += 1) {
			String[] questionPair = questionArray[j].split("=");
			String[] answerPair = answerArray[j].split("=");
			String[] commentPair = commentArray[j].split("=");

			String Question = questionPair[1];
			String Answer = answerPair[1];
			String Comment = commentPair[1];
			
			mFUTSurveyInfoList.add(new FUTSurveyInfo(Question, Answer, Comment));
			//mFUTSurveyInfoList.size();
			}	
		return true;
		}

	private void SaveFUTSurveyInfo(){
		//TODO: Save Linked List Data to File
		boolean result = false;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("######################################################" + "\n");
		buffer.append("################# D850 Questionnaire #################" + "\n");
		buffer.append("######################################################" + "\n");
		
		String Question = null;
		String Answer = null;
		String Comment = null;
		
		for(int i=0; i < numberOfQuestions; i++ ){
			Question = mFUTSurveyInfoList.get(i).GetQuestion();
			Answer = mFUTSurveyInfoList.get(i).GetAnswer();
			Comment = mFUTSurveyInfoList.get(i).GetComment();
			
			buffer.append("Question=" + Question + "\n");
			buffer.append("Answer=" + Answer + "\n");
			buffer.append("Comment=" + Comment + "\n");
			buffer.append("******************************************************"+"\n");
		}
		try{
			//File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/FUTSurvey/d850fut_questionnaire.txt");
			//File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/d850fut.txt");
			
			File FUTSurveyFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey");
			if(!FUTSurveyFolder.exists()) {
				FUTSurveyFolder.mkdirs();
			}
			File f = new File(Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey/d850fut_questionnaire.txt");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			//FileOutputStream fout = openFileOutput("d850fut.txt", MODE_PRIVATE);

			fos.write(buffer.toString().getBytes());
			fos.close();
		
			result = true;
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private void RadioButtonCreate(){
		//int tmpRB = Integer.parseInt(mFUTSurveyInfoList.get(mStep).GetAnswer());
		//CheckRBbutton(tmpRB);
		
		rb_group = (RadioGroup) findViewById(R.id.rb_group);
		rb1 = (RadioButton) findViewById(R.id.rb_1);
		rb2 = (RadioButton) findViewById(R.id.rb_2);
		rb3 = (RadioButton) findViewById(R.id.rb_3);
		rb4 = (RadioButton) findViewById(R.id.rb_4);
		rb5 = (RadioButton) findViewById(R.id.rb_5);
		
		rb_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButtonHandler(group, checkedId);

			}
			
		});
	}

	private void RadioButtonHandler(RadioGroup group, int checkedId){
		// TODO Auto-generated method stub
		String rb = null;
		
		if(checkedId == R.id.rb_1){
			rb = "1";
		}else if(checkedId == R.id.rb_2){
			rb = "2";
		}else if (checkedId == R.id.rb_3){
			rb ="3";
		}else if(checkedId == R.id.rb_4){
			rb ="4";
		}else if(checkedId == R.id.rb_5){
			rb ="5";
		}
		mFUTSurveyInfoList.get(mStep).SetAnswer(rb);
	}
	
	private void NextButtonCreate() {
		Button nextButton = (Button) findViewById(R.id.btn_next);
		nextButton.setOnClickListener(new View.OnClickListener() {
		

			@Override
			public void onClick(View v) {
				if(mStep < numberOfQuestions){
					NextButtonHandler();
				}
			}
			
		});
	}

	private void NextButtonHandler(){
		// 1) Save current data (Question, Answer, Comment) to linked list element in current step (mStep)
		//1-1) Question doesn't need to be saved in Linked List
		//1-2) RadioButton value already saved in Linked List by RadioButton handler
		//1-3) Save comment in linked list
		String p_comment = et_comment.getText().toString();
		mFUTSurveyInfoList.get(mStep).SetComment(p_comment);
		//RadioButtonHandler();
		//mFUTSurveyInfoList.get(mStep).SetAnswer(Integer.toString(tempCheckedRBId));
		//SaveFUTSurveyInfo();
		
		//2) Display next question
		
		// Stop increasing mStep after reaching the last question
		//if (mStep < numberOfQuestions - 1)
			mStep++;
		if(mStep > mFUTSurveyInfoList.size()-1){
			//Prepare to email survey data to NVG
			LoadOKCancelDialog("Send Email");
			mStep--; // come back to the last (final) question
		}else{
			LoadQuestion();
		}
		
		
	}
	private void BackButtonCreate() {
		Button backButton = (Button) findViewById(R.id.btn_back);
		backButton.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mStep >= 0){
					BackButtonHandler();
				}
				//tv_question.setText(mFUTSurveyInfoList.get(step.GetStepSurvey()).GetQuestion());				
				//step.SubtractStepSurvey();
			}
		});
	}

	private void BackButtonHandler(){
		// 1) Save current data (Question, Answer, Comment) to linked list element in current step (mStep)
		//1-1) Question doesn't need to be saved in Linked List
		//1-2) RadioButton value already saved in Linked List by RadioButton handler
		//1-3) Save comment in linked list
		String p_comment = et_comment.getText().toString();
		mFUTSurveyInfoList.get(mStep).SetComment(p_comment);
		//RadioButtonHandler();
		//mFUTSurveyInfoList.get(mStep).SetAnswer(Integer.toString(tempCheckedRBId));
		//SaveFUTSurveyInfo();
		
		//2) Display previous question
		mStep--;
		if(mStep < 0){
			//User pressed back button on Question #1 UI. //Going back to main menu
			//Before move back to main menu, save survey info to file
			SaveFUTSurveyInfo();
			
			//Going back to main menu...
			Intent intent = new Intent();
			setResult(BACK_TO_MAIN, intent);
			//Finish this activity to go back to Menu right away
			finish();
			
		}else{
			LoadQuestion();
		}	
		
	}
	
private void LoadLayout(){
		
		//1) display Question for the step
		tv_question = (TextView) findViewById(R.id.tv_question);
		
		//2) Display RB Group layout
		RadioButtonCreate();
		
		//3) Display Comment area
		et_comment = (EditText) findViewById(R.id.et_comment);
		
		//4) Next Button
		NextButtonCreate();
		
		//5) Back Button
		BackButtonCreate();
		
		
		
	}
	
	private int LoadQuestion(){
		if(mStep < 0 || mStep > mFUTSurveyInfoList.size() ){
			return -1;
		}else{
			//1) display Question for the step
			tv_question.setText(mFUTSurveyInfoList.get(mStep).GetQuestion());
							
			//2) Determine which Radio Button to enable
			CheckRBbutton();
				
			//4) Display Comment area
			et_comment.setText(mFUTSurveyInfoList.get(mStep).GetComment());
			
			return 0;
		}
		
	}
	
	private void SendEmail(){
		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "lgtestjwp@gmail.com", null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "NVG FUT Survey Answers");
		emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey/d850fut_questionnaire.txt"));
		startActivity(Intent.createChooser(emailIntent, "Send survey answers via..."));
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.survey, menu);
		return true;
	}

	public void onPause() {
		super.onPause();
		
		//Save survey data in file
		SaveFUTSurveyInfo();
		//Save current step in shared preference so that they can be restored on Resume
		SharedPreferences.Editor editor = getPreferences(0).edit();
		editor.putInt("CurrentStep", mStep);
		editor.commit();
		//Toast.makeText(getApplicationContext(), "File Saved", Toast.LENGTH_LONG);
	}
	
	public void onStop() {
		super.onStop();
		SaveFUTSurveyInfo();
		//Toast.makeText(getApplicationContext(), "File Saved", Toast.LENGTH_LONG);
	}
	
	   private void LoadErrorDialog(String err_no)
		{

			if (dialog == null || dialog.isShowing()==false) {
				
				//Create a dialog
				dialog = new Dialog(this);
				dialog.setContentView(R.layout.activity_dialog);
				
				//Set dialog title
				dialog.setTitle(getString(R.string.error)+ " " + err_no);
				
				//Set dialog message
				TextView dialogText = (TextView) dialog.findViewById(R.id.tv_dialog);
				dialogText.setGravity(Gravity.LEFT);
				dialogText.setText(getString(R.string.cannot_open_file));
		
				//Create OK button
				Button buttonOK = (Button) dialog.findViewById(R.id.ok_button_dialog);
				buttonOK.setText(R.string.ok_button);
				buttonOK.setVisibility(View.VISIBLE);
				
				//Set ClickListener to No button
				buttonOK.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						
					}
				});

				dialog.setOnDismissListener(new OnDismissListener() {
					public void onDismiss(DialogInterface dialogInterface) {
						Intent intent = new Intent();
						setResult(FILE_OPEN_FAIED, intent);
						//Finish this activity to go back to Menu right away
						finish();
					}
				});
				
				dialog.show();
				
			}
		}//End of LoadErrorDialog
	   
	   
private void LoadOKCancelDialog(String title)
	{

		if (dialog == null || dialog.isShowing()==false) {
				
			//Create a dialog
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.activity_dialog);
				
			//Set dialog title
			dialog.setTitle(title);
				
			//Set dialog message
			TextView dialogText = (TextView) dialog.findViewById(R.id.tv_dialog);
			dialogText.setGravity(Gravity.LEFT);
			dialogText.setPadding(20, 20, 20, 20);
			dialogText.setText(getString(R.string.send_email_survey));
		
			//Create OK button
			Button buttonOK = (Button) dialog.findViewById(R.id.ok_button_dialog);
			buttonOK.setText(R.string.ok_button);
			buttonOK.setVisibility(View.VISIBLE);
				
			//Set ClickListener to No button
			buttonOK.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dialog.dismiss();
						
				}
			});

			dialog.setOnDismissListener(new OnDismissListener() {
				public void onDismiss(DialogInterface dialogInterface) {
					//TODO: Call function for send an email
					SendEmail();
				}
			});
				
			dialog.show();
				
		}
	}//End of LoadErrorDialog

}


