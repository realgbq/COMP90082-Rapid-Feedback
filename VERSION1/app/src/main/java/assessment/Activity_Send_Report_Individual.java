/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package assessment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feedback.Activity_Login;
import com.example.feedback.Activity_Record_Voice;
import com.example.feedback.R;

import java.math.BigDecimal;
import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.*;

public class Activity_Send_Report_Individual extends AppCompatActivity {
    private int indexOfProject;
    private int indexOfStudent;
    private int indexOfGroup;
    private Toolbar mToolbar;
    private String from;
    public static final String FROMREALTIMESEND = "realtime_send";
    public static final String FROMREVIEWSEND = "review_send";
    private Handler handler;
    private Project project;
    private ProjectStudent student;
    private Remark remark;
    private ArrayList<Remark> remarkList;
    private static final String FROM_INDIVIDUAL = "FROM_INDIVIDUAL";
    private ArrayList<FinalRemark> finalRemarkList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_report_individual);

        initToolbar();
        Intent intent = getIntent();
        indexOfProject = Integer.parseInt(intent.getStringExtra("indexOfProject"));
        indexOfStudent = Integer.parseInt(intent.getStringExtra("indexOfStudent"));
        indexOfGroup = Integer.parseInt(intent.getStringExtra("indexOfGroup"));
        from = intent.getStringExtra("from");
        init();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        return true;
    }

    private void initToolbar() {
        mToolbar = findViewById(R.id.toolbar_send_report_individual);
        mToolbar.setTitle("Report -- Welcome, " + AllFunctions.getObject().getUsername() + " [ID: " + AllFunctions.getObject().getId() + "]");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_logout:
                        Toast.makeText(Activity_Send_Report_Individual.this, "Log out!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Send_Report_Individual.this,
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

    public void onBackPressed() {
        finish();
    }

    private void init() {
        handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 130:
                        Toast.makeText(Activity_Send_Report_Individual.this, "Successfully send end final result", Toast.LENGTH_SHORT).show();
                        break;
                    case 131:
                        Toast.makeText(Activity_Send_Report_Individual.this, "Fail to send the final result", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
        AllFunctions.getObject().setHandler(handler);

        project = AllFunctions.getObject().getProjectList().get(indexOfProject);
        student = AllFunctions.getObject().getProjectList().get(indexOfProject).getStudentList().get(indexOfStudent);
        finalRemarkList = student.getFinalRemarkList();
        remarkList = student.getRemarkList();

        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == AllFunctions.getObject().getId()) {
                remark = remarkList.get(i);
                break;
            }
        }

        Button button_record = findViewById(R.id.button_record_individual);
        button_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Record_Voice.class);
                intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                intent.putExtra("from", from);
                startActivity(intent);
            }
        });
        Button button_sendSingle = findViewById(R.id.button_sendStudent_sendReportIndividual);
        button_sendSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Editable_Group_Report.FROMREALTIMEEDIT)) {
                    AllFunctions.getObject().sendEmail(project.getId(), student.getId(), 1);
                    Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    AllFunctions.getObject().sendEmail(project.getId(), student.getId(), 1);
                    Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWSEND);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button button_sendBoth = findViewById(R.id.button_sendBoth_sendReportIndividual);
        button_sendBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Editable_Group_Report.FROMREALTIMEEDIT)) {
                    AllFunctions.getObject().sendEmail(project.getId(), student.getId(), 2);
                    Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    AllFunctions.getObject().sendEmail(project.getId(), student.getId(), 2);
                    Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWSEND);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button button_finish = findViewById(R.id.btn_finish_sendReportIndividual);
        button_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (from.equals(Activity_Editable_Group_Report.FROMREALTIMEEDIT)) {
                    Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREALTIMESEND);
                    startActivity(intent);
                    finish();
                } else if (from.equals(Activity_Editable_Group_Report.FROMREVIEWEDIT)) {
                    Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Display_Mark.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                    intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                    intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                    intent.putExtra("from", FROMREVIEWSEND);
                    startActivity(intent);
                    finish();
                }
            }
        });
        Button button_edit = findViewById(R.id.button_final_edit);
        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity_Send_Report_Individual.this, Activity_Final_Edit.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("indexOfProject", String.valueOf(indexOfProject));
                intent.putExtra("indexOfGroup", String.valueOf(indexOfGroup));
                intent.putExtra("indexOfStudent", String.valueOf(indexOfStudent));
                intent.putExtra("from", FROM_INDIVIDUAL);
                startActivity(intent);
                finish();
            }
        });

        TextView textView_totalMark = findViewById(R.id.textView_totalMark_sendReportIndividual);
        int finalMark = 0;
        if(finalRemarkList == null || finalRemarkList.size() <= 0) {
            finalMark = (int) getAverageMark(remarkList);
        } else {
            finalMark = (int) totalMark();
        }
        textView_totalMark.setText("Mark:" + finalMark + "%");

        String htmlString =
                "<html>" +
                        "<body>" +
                        "<h1 style=\"font-weight: normal\">" + project.getName() + "</h1>" +
                        "<hr>" +
                        "<p>" + student.getFirstName() + " " + student.getMiddleName() + " " + student.getLastName() + " --- " + student.getStudentNumber() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Subject</h2>" +
                        "<p>" + project.getSubjectCode() + " --- " + project.getSubjectName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Project</h2>" +
                        "<p>" + project.getName() + "</p >" +
                        "<h2 style=\"font-weight: normal\">Mark Attained</h2>" +
                        "<p>" + finalMark + "%</p >";

        htmlString = htmlString +
                "</p >" +
                "<br><br><br><hr>" +
                "<div>";

        htmlString += "<h2 style=\"font-weight: normal\"> Criteria</h2>" + "<p>";
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            double mark = getAverageCriterionMark(remarkList, remark.getAssessmentList().get(i).getCriterionId());
            if(finalRemarkList != null && finalRemarkList.size() > 0){
                for(FinalRemark finalRemark : finalRemarkList){
                    if(remark.getAssessmentList().get(i).getCriterionId() == finalRemark.getCriterionId()){
                        mark = finalRemark.getFinalScore();
                    }
                }
            }
            htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + getCriterionName(remark.getAssessmentList().get(i)) + "</span>" +
                    "<span style=\"float:right\">" + "  ---  " + mark + "/" + getCriterionMaxMark(remark.getAssessmentList().get(i)) + "</span></h3>";
            for (int j = 0; j < remarkList.size(); j++) {
                htmlString += "<h4 style=\"font-weight: normal;color: #014085\">" + "Marker " + (j + 1) + ":</h4>";
                if (remarkList.get(j).getAssessmentList().size() > 0) {
                    for (int k = 0; k < remarkList.get(j).getAssessmentList().get(i).getSelectedCommentList().size(); k++) {
                        htmlString += "<p>" + getFieldName(remarkList.get(j).getAssessmentList().get(i).getSelectedCommentList().get(k)) +
                                " : " + getExCommentName(remarkList.get(j).getAssessmentList().get(i).getSelectedCommentList().get(k)) + "</p >";
                    }
                }
            }
            htmlString += "<br>";
        }

        htmlString += "<h3 style=\"font-weight: normal\"><span style=\"float:left\">" + "Remark" + "</span></h3>";
        for (int j = 0; j < remarkList.size(); j++) {
            htmlString += "<h4 style=\"font-weight: normal;color: #014085\">" + "Marker " + (j + 1) + ":</h4>";
            if (remarkList.get(j).getText() == null) {
                htmlString += "<p>" + "No remark." + "</p >";
            } else {
                htmlString += "<p>" + remarkList.get(j).getText() + "</p >";
            }
        }

        htmlString +=
                "</div>" +
                        "</body>" +
                        "</html>";
        TextView textView_pdfContent = findViewById(R.id.textView_pdfContent_sendReportIndividual);
        textView_pdfContent.setText(Html.fromHtml(htmlString));
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

    public String getFinalRemark(ArrayList<Remark> remarkList) {
        Remark remark = new Remark();
        for (int i = 0; i < remarkList.size(); i++) {
            if (remarkList.get(i).getId() == project.getPrincipalId()) {
                remark = remarkList.get(i);
            }
        }

        String finalRemark = "";
        for (int i = 0; i < remark.getAssessmentList().size(); i++) {
            finalRemark += "Critrion" + (i + 1) + ": " + getCriterionName(remark.getAssessmentList().get(i));
            finalRemark += "\n";
            for (int k = 0; k < remark.getAssessmentList().get(i).getSelectedCommentList().size(); k++) {
                finalRemark += getFieldName(remark.getAssessmentList().get(i).getSelectedCommentList().get(k)) +
                        " : " + getExCommentName(remark.getAssessmentList().get(i).getSelectedCommentList().get(k)) + "\n";
            }
        }
        return finalRemark;
    }

    public double getAverageCriterionMark(ArrayList<Remark> remarkList, int criterionId) {
        double sumMark = 0;
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

    public double getAverageMark(ArrayList<Remark> remarkList) {
        double sumMark = 0;
        double markers = remarkList.size();
        for (int i = 0; i < markers; i++) {
            sumMark += getTotalMark(remarkList.get(i));
        }
        double avgMark = sumMark / markers;
        BigDecimal bigDecimal = new BigDecimal(avgMark);
        avgMark = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
        return avgMark;
    }

    public double getTotalMark(Remark remark) {
        Double sum = 0.0;

        double totalWeight = 0;
        int totalWeighting = 0;

        for (int j = 0; j < project.getCriterionList().size(); j++) {
            totalWeight = totalWeight + project.getCriterionList().get(j).getMaximumMark();
        }
        totalWeighting = Double.valueOf(totalWeight).intValue();

        for (int m = 0; m < remark.getAssessmentList().size(); m++) {
            sum = sum + remark.getAssessmentList().get(m).getScore();
        }
        sum = sum * (100.0 / totalWeighting);

        return sum;
    }

    public String getCriterionName(Assessment assessment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (project.getCriterionList().get(i).getId() == assessment.getCriterionId()) {
                return project.getCriterionList().get(i).getName();
            }
        }
        return "";
    }

    public double getCriterionMaxMark(Assessment assessment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            if (project.getCriterionList().get(i).getId() == assessment.getCriterionId()) {
                return project.getCriterionList().get(i).getMaximumMark();
            }
        }
        return 0;
    }

    public String getFieldName(SelectedComment selectedComment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                if (project.getCriterionList().get(i).getFieldList().get(j).getId() == selectedComment.getFieldId()) {
                    return project.getCriterionList().get(i).getFieldList().get(j).getName();
                }
            }
        }
        return "";
    }

    public String getExCommentName(SelectedComment selectedComment) {
        for (int i = 0; i < project.getCriterionList().size(); i++) {
            for (int j = 0; j < project.getCriterionList().get(i).getFieldList().size(); j++) {
                for (int k = 0; k < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().size(); k++) {
                    for (int m = 0; m < project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().size(); m++) {
                        ExpandedComment expandedComment = project.getCriterionList().get(i).getFieldList().get(j).getCommentList().get(k).getExpandedCommentList().get(m);
                        if (expandedComment.getId() == selectedComment.getExCommentId()) {
                            return expandedComment.getText();
                        }
                    }
                }
            }
        }
        return "";
    }
}
