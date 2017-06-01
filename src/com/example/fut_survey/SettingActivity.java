package com.example.fut_survey;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingActivity extends Activity {

	private EditText modelName;
	private EditText swVersion;
	private EditText participantName;
	private EditText nvgEmail;
	//private Button saveSettingButton;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    // TODO Auto-generated method stub
	    setContentView(R.layout.activity_settings);
	}
	
	protected void onResume(){
		super.onResume();
		loadLayout();
		readSetting();
		
	}
	
	private void loadLayout(){
	    modelName = (EditText) findViewById(R.id.et_model_name);
	    swVersion = (EditText) findViewById(R.id.et_sw_version);
	    participantName = (EditText) findViewById(R.id.et_participant_name);
	    nvgEmail = (EditText) findViewById(R.id.et_nvg_email);
	   
	    saveButton();
	}
	
	private boolean readSetting(){
		String fileContents = null;
		File f = new File (Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey/futinfo.txt");
		if (!f.exists()){
			return false;
		}
		try{
			InputStream in = new FileInputStream(f);
			BufferedReader reader = null;
			
			if (in != null){
				reader = new BufferedReader(new InputStreamReader(in));
			}
			
			StringBuffer buffer = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			
			if (buffer.length() == 0){
				return false;
			}
			reader.close();
			in.close();
			fileContents = buffer.toString();
			
		} catch (IOException e) {
			Log.e("SpotDiffGame", "exception in reading game data file");
			return false;
		}
	
		
		String[] lineArray = fileContents.split("\n");

		String[] ModelNamePair = lineArray[0].split("=");
		String[] SWVersionPair = lineArray[1].split("=");
		String[] ParticipantNamePair = lineArray[2].split("=");
		String[] NVGEmailPair = lineArray[3].split("=");
		
		String ModelName = ModelNamePair[1];
		String SWVersion = SWVersionPair[1];
		String Participant = ParticipantNamePair[1];
		String NVGEmail = NVGEmailPair[1];
	
		modelName.setText(ModelName);
		swVersion.setText(SWVersion);
		participantName.setText(Participant);
		nvgEmail.setText(NVGEmail);
			
		return false;	
	}
	
	private void saveButton(){
	    Button saveSettingButton = (Button) findViewById(R.id.bt_save_setting);
	    saveSettingButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SaveSetting();
			}
		});

	}

	private void SaveSetting(){
	    String ModelName = modelName.getText().toString();
	    String SWVersion = swVersion.getText().toString();
	    String ParticipantName = participantName.getText().toString();
	    String NVGEmail = nvgEmail.getText().toString();
		
	    StringBuffer buffer = new StringBuffer();
	    buffer.append("ModelName=" + ModelName + "\n");
	    buffer.append("SwVersion=" + SWVersion + "\n");
	    buffer.append("ParticipantName=" + ParticipantName + "\n");
	    buffer.append("NVGEmail=" + NVGEmail + "\n");
	    
	    try{
			File FUTSurveyFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey");
			if(!FUTSurveyFolder.exists()) 
				FUTSurveyFolder.mkdirs();
			File f = new File(Environment.getExternalStorageDirectory().getPath() + "/FUTSurvey/futinfo.txt");
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			//FileOutputStream fout = openFileOutput("d850fut.txt", MODE_PRIVATE);

			fos.write(buffer.toString().getBytes());
			fos.close();
			
	    } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
	}}
	

