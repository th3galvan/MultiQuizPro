package galvan.multiquizpro;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {
    public static final String CORRECT_ANSWER = "correct_answer";
    public static final String CURRENT_QUESTION = "current_question";
    public static final String ANSWER_IS_CORRECT = "answer_is_correct";
    public static final String ANSWER = "answer";
    final private int ids_answers[]={R.id.answer1, R.id.answer2,R.id.answer3,R.id.answer4};


    private TextView text_questions;
    private RadioGroup group;
    private Button btn_next, btn_prev;
    private String[] all_questions;

    private int[] answer;
    private int correct_answer;
    private int current_question;
    private boolean[] answer_is_correct;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CORRECT_ANSWER,correct_answer);
        outState.putInt(CURRENT_QUESTION,current_question);
        outState.putBooleanArray(ANSWER_IS_CORRECT,answer_is_correct);
        outState.putIntArray(ANSWER,answer);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        //OBTENEMOS REFERENCIAS SOBRE LAS COSASS DE LA PANTALLA
        text_questions=(TextView) findViewById(R.id.text_question);                                 // ""
        group =(RadioGroup)findViewById(R.id.answer_group);                                         // ""
        btn_next = (Button)findViewById(R.id.btn_check);
        btn_prev = (Button)findViewById(R.id.btn_prev);
        all_questions =getResources().getStringArray(R.array.all_questions);                        //Alt enter en all_questions o y creamos un campo

        if(savedInstanceState==null)
        {   startOver();

        }else{
            Bundle state= savedInstanceState;
            correct_answer=state.getInt(CORRECT_ANSWER);
            current_question=state.getInt(CURRENT_QUESTION);
            answer_is_correct=state.getBooleanArray(ANSWER_IS_CORRECT);
            answer=state.getIntArray(ANSWER);
            show_question();
        }


        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer();
                if(current_question<all_questions.length-1)
                {
                    current_question++;                                                                 //Sumamos uno al current qstion
                    show_question();                                                                    //Llamamos otra pregunta
                }
                else
                {
                    checkResults();
                }



            }
        });
        btn_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {   checkAnswer();
                if(current_question>0)
                {
                    current_question--;
                    show_question();
                }
            }
        });
    }

    private void startOver() {
        answer_is_correct = new boolean[all_questions.length];                                                                                            //   String[] all_questions =getResources().getStringArray(R.array.all_questions);         no queremos estas variables asi, las declaramos fuera de onCreate como un campo
        answer =new int[all_questions.length];
        for(int i=0; i<answer.length;i++)
        {
            answer[i]=-1;
        }
        current_question=0;
        show_question();
    }

    private void checkResults() {
        int correctas=0, nocontestadas=0;
        int incorrectas=0;
        for(int i= 0; i<all_questions.length;i++)
        {
            if(answer_is_correct[i]) correctas++;
            else if (answer[i]==-1)nocontestadas++;
            else incorrectas++;

        }
        String message = String.format("Correctas: %d\nIncorrectas %d\nNo contestadas %d", correctas, incorrectas, nocontestadas );
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.results);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(R.string.start_over, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startOver();
            }
        });
        builder.create().show();

    }

    private void checkAnswer() {
        int id = group.getCheckedRadioButtonId();
        int ans = -1;
        for (int i=0; i<ids_answers.length;i++)
        {
            if(ids_answers[i]==id)
            {
                ans=i;
            }
        }
        answer_is_correct[current_question] = (ans == correct_answer);                   //guarda si se ha contestado bien o mal la pregunta
        answer[current_question]=ans;
    }


    private void show_question() {                                                                  //hemos creado este metodo seleccionandolo to y dandole refactor extract y method o Ctrl+Alt+M
        //TextView text_question = ; String all_question = ; borramos esto y las declaramos de otra manera **1**
        String q = all_questions[current_question];                                                                   //Sacamos la info de la pregunta 0 del array all_questions
        String[] parts = q.split(";");                                                              //Partimos el string en trozos con ;

        group.clearCheck();                                                                         //Quitamos la seleccion de la pregunta anterior

        text_questions.setText(parts[0]);
        // String[] answers = getResources().getStringArray(R.array.answers); ya no va asi

        for(int i=0; i < ids_answers.length;i++)
        {
            RadioButton rb= (RadioButton) findViewById(ids_answers[i]);
            String ans = parts[i+1];
            if(ans.charAt(0)=='*'){                                                              //Ojo que si pones "" es para strings y '' es para caracteres
                correct_answer = i;                                                                 //Estamos creando la variable correct_answer nos sale en rojo, le damos Alt+Enter y le damos a crear campo
                ans = ans.substring(1);                                                       // enseña el string a partir del caracter 1
            }
            rb.setText(ans);
            if(answer[current_question]==i)
            {
                rb.setChecked(true);
            }
        }
        if(current_question==0)
        {
            btn_prev.setVisibility(View.INVISIBLE);
        }
        else
        {
            btn_prev.setVisibility(View.VISIBLE);
        }
        if(current_question == all_questions.length-1)
        {
            btn_next.setText(R.string.finish);
        }
        else
        {
            btn_next.setText(R.string.next);
        }
    }
}
