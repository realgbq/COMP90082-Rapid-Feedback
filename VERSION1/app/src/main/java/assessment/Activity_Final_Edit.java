package assessment;

/**
 * Activity of final edit
 *
 * Created by xiyang on 2020/5/2
 */
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.*;
import com.alibaba.fastjson.JSON;
import com.example.feedback.Activity_Login;
import com.example.feedback.R;
import main.AllFunctions;
import newdbclass.*;
import org.apache.xmlbeans.impl.xb.xsdschema.All;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Activity_Final_Edit extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfGroup;
    private Remark remark;
    private int indexOfStudent;
    ArrayList<Integer> studentList;
    private Toolbar mToolbar;
    private String from;
    private Handler handler;
    private Project project;
    private ProjectStudent student;
    private ArrayList<Remark> remarkList;
    private ArrayList<Criterion> criteriaList;
    private ArrayList<FinalRemark> finalRemarkList;

    private static final String FROM_GROUP = "FROM_GROUP";
    private static final String FROM_INDIVIDUAL = "FROM_INDIVIDUAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_edit);
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));

        //init data
        from = intent.getStringExtra("from");
        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = project.getStudentList().get(indexOfStudent);
        remarkList = student.getRemarkList();
        finalRemarkList = student.getFinalRemarkList();
        studentList = new ArrayList<Integer>();
        if (indexOfGroup == 0) {
            studentList.add(indexOfStudent);
        } else {
            for (int i = 0; i < project.getStudentList().size(); i++) {
                if (project.getStudentList().get(i).getGroupNumber() == indexOfGroup) {
                    studentList.add(i);
                }
            }
        }
        if(finalRemarkList == null || finalRemarkList.size() <= 0){
            // set initial list
            for(Criterion criterion : project.getCriterionList()){
                finalRemarkList.add(new FinalRemark(criterion.getId(), getAverageCriterionMark(remarkList, criterion.getId())));
            }
        }

        // setHandler
        bindHandler();
        AllFunctions.getObject().setHandler(handler);

        // init tool bar
        initToolbar();
        for (int i = 0; i < student.getRemarkList().size(); i++) {
            if (student.getRemarkList().get(i).getId() == AllFunctions.getObject().getId()) {
                remark = student.getRemarkList().get(i);
                break;
            }
        }

        //set list view
        MyAdapter myAdapter = new MyAdapter(finalRemarkList, this);
        ListView lv_final_individual = findViewById(R.id.lv_final_individual);
        lv_final_individual.setAdapter(myAdapter);
        setListViewHeightBasedOnChildren(lv_final_individual);

        //init button and text
        init();
    }

    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_final_edit);
        mToolbar.setTitle("Final Edit For Group: " + student.getGroupNumber());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                goBack();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Final_Edit.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Final_Edit.this,
                                Activity_Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void bindHandler() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 132:
                        Toast.makeText(Activity_Final_Edit.this,
                                "Success!", Toast.LENGTH_SHORT).show();
                        goBack();
                        break;
                    case 133:
                        Toast.makeText(Activity_Final_Edit.this,
                                "Server error. Please try again", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);
    }

    private void init(){
        TextView textView_totalMark_final_edit = findViewById(R.id.textView_totalMark_final_edit);
        textView_totalMark_final_edit.setText(String.format("Mark:%.2f", totalMark()) + "%");
        Button cancel = findViewById(R.id.button_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        Button save = findViewById(R.id.button_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Integer studentIndex : studentList){
                    int studentId = project.getStudentList().get(studentIndex).getId();
                    AllFunctions.getObject().editFinalMark(project.getId(), studentId, JSON.toJSONString(finalRemarkList));
                }
            }
        });

    }

    private void goBack() {
        AllFunctions.getObject().syncProjectList();
        if (from.equals(FROM_GROUP)) {
            Intent intent = new Intent(Activity_Final_Edit.this, Activity_Send_Report_Group.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
            intent.putExtra("from", Activity_Editable_Group_Report.FROMREVIEWEDIT);
            startActivity(intent);
            finish();
        } else if (from.equals(FROM_INDIVIDUAL)) {
            Intent intent = new Intent(Activity_Final_Edit.this, Activity_Send_Report_Individual.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
            intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
            intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
            intent.putExtra("from", Activity_Editable_Individual_Report.FROMREVIEWEDIT);
            startActivity(intent);
            finish();
        }
    }

    private double getAverageCriterionMark(ArrayList<Remark> remarkList, int criterionId) {
        double sumMark = 0d;
        double markers = remarkList.size();
        for (int i = 0; i < markers; i++) {
            Remark remark = remarkList.get(i);
            for (int j = 0; j < remark.getAssessmentList().size(); j++) {
                if (remark.getAssessmentList().get(j).getCriterionId() == criterionId) {
                    sumMark += remark.getAssessmentList().get(j).getScore();
                }
            }
        }
        double avgMark = sumMark / markers;
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return avgMark;
    }

    private double totalMark(){
        double sum = 0d;
        double total = 0d;
        for(Criterion criterion : project.getCriterionList()){
            sum += criterion.getMaximumMark();
            for(FinalRemark finalRemark : finalRemarkList){
                if(finalRemark.getCriterionId() == criterion.getId()){
                    total += finalRemark.getFinalScore();
                }
            }
        }
        return total * (100.0 / sum);
    }

    public class MyAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<FinalRemark> finalRemarkList;
        private Double increment = 0.0;

        public MyAdapter(ArrayList<FinalRemark> finalRemarkList, Context context) {
            this.finalRemarkList = finalRemarkList;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return finalRemarkList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_final_criteria, parent, false);
            final View view10 = convertView;
            TextView tv_final_criteria_name = convertView.findViewById(R.id.tv_final_criteria_name);
            tv_final_criteria_name.setText(project.getCriterionList().get(position).getName());

            if (project.getCriterionList().get(position).getMarkIncrement() == 1) {
                increment = 1.0;
            } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.5) {
                increment = 0.5;
            } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.25) {
                increment = 0.25;
            }

            FinalRemark finalRemark = finalRemarkList.get(position);
            SeekBar seekBar = view10.findViewById(R.id.seek_bar_mark);
            seekBar.setMax((int) (project.getCriterionList().get(position).getMaximumMark() / increment));
            seekBar.setProgress(Double.valueOf(finalRemark.getFinalScore() / increment).intValue());
            TextView textView = view10.findViewById(R.id.textview_mark);
            textView.setText(finalRemark.getFinalScore() + " / " + Double.valueOf(project.getCriterionList().get(position).getMaximumMark()));

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (project.getCriterionList().get(position).getMarkIncrement() == 1) {
                        increment = 1.0;
                    } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.5) {
                        increment = 0.5;
                    } else if (project.getCriterionList().get(position).getMarkIncrement() == 0.25) {
                        increment = 0.25;
                    }
                    Double progressDisplay = progress * increment;
                    TextView textView = view10.findViewById(R.id.textview_mark);
                    textView.setText(progressDisplay + " / " + Double.valueOf(project.getCriterionList().get(position).getMaximumMark()));

                    int criterionId = project.getCriterionList().get(position).getId();
                    for(FinalRemark newRemark : finalRemarkList){
                        if(newRemark.getCriterionId() == criterionId){
                            newRemark.setFinalScore(progressDisplay);
                        }
                    }
                    TextView textView_totalMark_final_edit = findViewById(R.id.textView_totalMark_final_edit);
                    textView_totalMark_final_edit.setText(String.format("Mark:%.2f", totalMark()) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            return convertView;
        }

    }

}
