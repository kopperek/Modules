package app.android.kopper.selectfile;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by kopper on 2015-02-14.
 * (C) Copyright 2015 kopperek@gmail.com
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */
public class SelectDirectoryActivity extends ListActivity {

    public static final String SELECTED_DIRECTORY="selected.directory";

    public static final String DIR="param.dir";
    public static final String SELECT_DIR="param.selectdir";
    public static final String MESSAGE="param.message";

    private DirListAdapter listAdapter;

    private boolean selectDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_folder);
        listAdapter=new DirListAdapter(getApplicationContext());
        setListAdapter(listAdapter);
        Bundle extras=getIntent().getExtras();
        String currentDirPath=(String)getLastNonConfigurationInstance();
        if(currentDirPath==null)
            currentDirPath=(String)getInitialValue(extras,DIR,"/");
        File currentDir=new File(currentDirPath);
        if(!currentDir.exists()||!currentDir.isDirectory())
            currentDir=new File("/");
        setCurrentDir(currentDir);

        selectDir=((Boolean)getInitialValue(extras,SELECT_DIR,Boolean.TRUE)).booleanValue();
        LogUtil.i("selectDir: "+selectDir);
        View okButton=findViewById(R.id.ok_button);
        if(!selectDir) {//file selection dialog
            ((ViewGroup)okButton.getParent()).removeView(okButton);
        } else {
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResultAndClose(listAdapter.getCurrentDir());
                }
            });
        }

        String message=(String)getInitialValue(extras,MESSAGE,null);
        TextView messageView=(TextView)findViewById(R.id.message);
        if(message==null)
            ((ViewGroup)messageView.getParent()).removeView(messageView);
        else
            messageView.setText(message);
    }

    private void setResultAndClose(File result) {
        Intent data=new Intent();
        data.putExtra(SELECTED_DIRECTORY,result);
        setResult(RESULT_OK,data);
        finish();
    }

    private Object getInitialValue(Bundle extras, String fieldName, Object defaultValue) {
        Object result=defaultValue;
        if(extras!=null) {
            Object providedValue=extras.get(fieldName);
            if(providedValue!=null)
                result=providedValue;
        }
        return result;
    }

    private void setCurrentDir(File dir) {
        listAdapter.setCurrentDir(dir);
        ((TextView)findViewById(R.id.current_dir)).setText(dir.getAbsolutePath());
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return listAdapter.getCurrentDir().getAbsolutePath();
    }

    @Override
    protected void onListItemClick(ListView l,View v,int position,long id) {
        File selectedFile=(File)listAdapter.getItem(position);
        if(selectedFile.isDirectory())
            setCurrentDir(selectedFile);
        else if(!selectDir) {
            setResultAndClose(selectedFile);
        }
    }
}
