package com.example.fut_survey;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	public static int COUNTER = 0;
	private static final int SURVEY_COMPLETED = 1;
	private static final int FILE_OPEN_FAIED = 2;
	private static final int BACK_TO_MAIN =3;
	protected static final int SURVEY = 0;
	private Boolean mContinueSurvey=false;
	private Button continueButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // create all the button objects
        final Button newButton =  (Button) findViewById(R.id.new_button);
        continueButton = (Button) findViewById(R.id.continue_button);
        Button settingButton = (Button) findViewById(R.id.setting_button);
        
        // TEST: can be deleted
        final TextView testView = (TextView) findViewById(R.id.test_name);
        
        //final Intent intentNew = new Intent (this, SurveyActivity.class);
        final Intent intentSetting = new Intent (this, SettingActivity.class);
        
        // Event-click for NEW BUTTON
        newButton.setOnClickListener(new View.OnClickListener() {
			      	       	
			@Override
			public void onClick(View v) {
				// Start New Survey
				Intent intentNew = new Intent(MainActivity.this, SurveyActivity.class);
			    intentNew.putExtra(SurveyActivity.NEW_SURVEY, true);
				//if(mPkgIdSelected == null){
				//	mPkgIdSelected = getString(R.string.pkg_id_default); //"default"
				//}
				//intent.putExtra(SpotDiffActivity.PACK_ID, mPkgIdSelected);
			    
				startActivityForResult(intentNew, SURVEY);
			}
        });
        
        // Event-click for CONTINUE BUTTON
        continueButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// Start New Survey
				Intent intentContinue = new Intent(MainActivity.this, SurveyActivity.class);
				intentContinue.putExtra(SurveyActivity.NEW_SURVEY, false);
			    
				startActivityForResult(intentContinue, SURVEY);
			}
		});
		
			
		// Event-click for SETTING BUTTON
        settingButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(intentSetting);
			}
		});
    }

    protected void onResume() {
    	super.onResume();
    	
    	SharedPreferences prefs = getPreferences(0); 
        // The second parameter in getBoolean is default value in case
        // the ContinueGmae does not exist
        mContinueSurvey =  prefs.getBoolean("ContinueSurvey", false);
        Log.v("MainActivity","onResume mContinueSurvey: " +mContinueSurvey);
        continueButton.setEnabled(mContinueSurvey);

    	
    	
    	
    }
    
    protected void onPause() {
    	super.onPause();
    	
    	// http://stackoverflow.com/questions/12828502/android-sharedpreferences-how-to-implement-them-properly

    	SharedPreferences.Editor editor = getPreferences(0).edit();
        editor.putBoolean("ContinueSurvey", mContinueSurvey);
        editor.commit();
        Log.v("MainActivity","onPause Saved mContinueSurvey: " +mContinueSurvey);
    	    
    	
    }    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, 
        Intent data) {
  	  SharedPreferences.Editor editor;
  	  
      super.onActivityResult(requestCode, resultCode, data);
      	  // Game completed
   	     editor = getPreferences(0).edit();
   	     
   	     if (resultCode == SURVEY_COMPLETED) {
   	    	 //Survey completed. Therefore, Continue button should be disabled??
      		 Log.v("MainActivity","onActivityResult requestCode: SURVEY_COMPLETED : Save ContinueGame False");
      		 editor.putBoolean("ContinueSurvey", false);//set ConitnueSurvy to false 
            }
      	  else if (resultCode == FILE_OPEN_FAIED){
      		  //Questionnaire file failure
      		  //Do nothing
      	  }
          else if (resultCode == BACK_TO_MAIN){
        	  //Back button.  Therfore, Continue button should be enabled.
        	  Button continueButton = (Button) findViewById(R.id.continue_button);
        	  continueButton.setEnabled(mContinueSurvey);
        	  
              Toast.makeText(this, "Survey Saved", Toast.LENGTH_SHORT).show();
        	  Log.v("DiffPic","onActivityResult requestCode: BACK_TO_MAIN : Save ContinueGame True");
      	      editor.putBoolean("ContinueSurvey", true); //Set ContinueSurvey to true
            }
   	      editor.commit();
      	 
      
    }//End of onAcitivtyResult 
    
}
