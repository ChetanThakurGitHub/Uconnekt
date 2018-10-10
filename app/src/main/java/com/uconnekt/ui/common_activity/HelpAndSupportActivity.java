package com.uconnekt.ui.common_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.adapter.listing.HandSAdapter;
import com.uconnekt.application.Uconnekt;
import com.uconnekt.model.HelpandSupport;

import java.util.ArrayList;

public class HelpAndSupportActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);
        initiView();
    }

    private void initiView(){
        ImageView iv_for_backIco = findViewById(R.id.iv_for_backIco);
        iv_for_backIco.setVisibility(View.VISIBLE);
        iv_for_backIco.setOnClickListener(this);
        TextView tv_for_tittle = findViewById(R.id.tv_for_tittle);
        tv_for_tittle.setText(getString(R.string.help_and_support));
        RecyclerView recycler_view = findViewById(R.id.recycler_view);
        findViewById(R.id.layoutSendFeed).setOnClickListener(this);
        findViewById(R.id.ivFacebook).setOnClickListener(this);
        findViewById(R.id.ivLinkedIn).setOnClickListener(this);
        findViewById(R.id.ivTwiter).setOnClickListener(this);
        findViewById(R.id.ivMail).setOnClickListener(this);

        //TextView tv_for_test = findViewById(R.id.tv_for_test);
        /*Spannable firstTxt = new SpannableString("Feed free to ");
        firstTxt.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.darkgray)), 0, firstTxt.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_for_test.setText(firstTxt);
        Spannable like = new SpannableString("contact us");
        like.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.yellow)), 0, like.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_for_test.append(like);*/

        TextView tv_for_contactUs = findViewById(R.id.tv_for_contactUs);
        tv_for_contactUs.setClickable(true);
        tv_for_contactUs.setMovementMethod(LinkMovementMethod.getInstance());
        String text = "<a href='https://www.connektus.com.au/'>Contact us</a>";
        tv_for_contactUs.setText(Html.fromHtml(text));

        setData(recycler_view);
    }

    private void setData(RecyclerView recycler_view){

        ArrayList<HelpandSupport> helpandSupportList = new ArrayList<>();
        HandSAdapter handSAdapter = new HandSAdapter(helpandSupportList);
        recycler_view.setAdapter(handSAdapter);
        recycler_view.setFocusable(false);
        recycler_view.setNestedScrollingEnabled(false);

        if (Uconnekt.session.getUserInfo().userType.equals("business")) {
            for (int i = 0; i < 9; i++) {
                HelpandSupport helpandSupport = new HelpandSupport();
                switch (i) {
                    case 0:
                        helpandSupport.question = "What are the benefits of using ConnektUs?";
                        helpandSupport.answer = "We allow Job Seekers and Employers to connekt very effectively. There are many ways to reach out to an employer that suits your needs and specializes in your area of specialty. All of this is available at your fingertips and on the go.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 1:
                        helpandSupport.question = "Can I include open jobs in my description?";
                        helpandSupport.answer = "Absolutely! You can include what you believe will attract people to reach out to you in search for their next role. Make it count!";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 2:
                        helpandSupport.question = "Can I create an Employer and a Job Seeker profile?";
                        helpandSupport.answer = "Of course, you can! Employer and Job Seeker profiles are treated separately with no relation between the two. Be sure to keep your profiles up to date and active as you never know what is around the corner.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 3:
                        helpandSupport.question = "My job title doesn’t appear in the drop-down list?";
                        helpandSupport.answer = "If your job title doesn’t appear in our list, please contact one of our friendly staff members. We will ensure your job title is included, so you can update your profile. An updated profile will give you the best chance of finding your potential future role.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 4:
                        helpandSupport.question = "What do I do if someone has left an offensive rating?";
                        helpandSupport.answer = "If someone has left an offensive rating against your profile, please contact one of our friendly staff so we can investigate the situation further. We can remove ratings should they not meet our guidelines. See our Terms & Conditions.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 5:
                        helpandSupport.question = "Can I see who has viewed my profile?";
                        helpandSupport.answer = "Absolutely! You will be notified every time a Job Seeker views your profile! Be sure to save profiles to your favourites list to never lose track!";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 7:
                        helpandSupport.question = "How does the search results work?";
                        helpandSupport.answer = "By default, the profiles readily available to view are based on location. This is to prevent irrelevant results showing. If you want to search for specific results, use our filter located to the top right of screen. Same applies for our MAP screen.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 8:
                        helpandSupport.question = "What is ‘area of specialty’ and why is it useful?";
                        helpandSupport.answer = "This will ensure that the right Job Seekers are connekting with you, so you can help them find their next role. We are all about making hiring as easy as possible.";
                        helpandSupportList.add(helpandSupport);
                        break;
                }
            }
        }else {
            for (int i = 0; i < 9; i++) {
                HelpandSupport helpandSupport = new HelpandSupport();
                switch (i) {
                    case 0:
                        helpandSupport.question = "What are the benefits of using ConnektUs?";
                        helpandSupport.answer = "We allow Job Seekers and Employers to connekt very effectively. There are many ways to reach out to an employer that suits your needs and specializes in your area of specialty. All of this is available at your fingertips.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 1:
                        helpandSupport.question = "What are the benefits of adding strengths and values?";
                        helpandSupport.answer = "Most employers are interested in specific potential candidates for current and upcoming roles. Defining your strengths and values will better align you to the right employer so they can assist you. Remember, there are only three of each, so choose wisely!";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 2:
                        helpandSupport.question = "Can I create an Employer and a Job Seeker profile?";
                        helpandSupport.answer = "Of course, you can! Employer and Job Seeker profiles are treated separately with no relation between the two. Be sure to keep your profiles up to date and active as you never know what is around the corner.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 3:
                        helpandSupport.question = "My job title doesn’t appear in the drop-down list?";
                        helpandSupport.answer = "If your job title doesn’t appear in our list, please contact one of our friendly staff members. We will ensure your job title is included, so you can update your profile. An updated profile will give you the best chance of finding your potential future role.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 4:
                        helpandSupport.question = "Is ConnektUs free to use?";
                        helpandSupport.answer = "ConnektUs is a free platform for all Job Seekers. We understand the importance of finding the right role for you and that shouldn’t come at a cost.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 5:
                        helpandSupport.question = "Can I see who has viewed my profile?";
                        helpandSupport.answer = "Absolutely! Not only will you get a notification each time your profile is viewed by an employer. You can also see who has viewed your profile in your ‘profile’ screen under ‘views’. Click and chat!";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 7:
                        helpandSupport.question = "How does the search results work?";
                        helpandSupport.answer = "By default, the profiles readily available to view are based on your location. This is to prevent irrelevant results showing. If you want to search for specific results, use our filter located to the top right of screen. Same applies for our MAP screen.";
                        helpandSupportList.add(helpandSupport);
                        break;
                    case 8:
                        helpandSupport.question = "What is ‘area of specialty’ and why is it useful?";
                        helpandSupport.answer = "Employers are looking for very specific candidates with certain experiences. Area of specialty defines what industry you have the most amount of experience which will attract the right employers to your profile.";
                        helpandSupportList.add(helpandSupport);
                        break;
                }
            }
        }
        handSAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_for_backIco:
                onBackPressed();
                break;
            case R.id.layoutSendFeed:
                startActivity(new Intent(this,FeedbackActivity.class));
                break;
            case R.id.ivFacebook:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/pages/category/Information-Technology-Company/ConnektUs-412365742629253/"));
                startActivity(intent);
                break;
            case R.id.ivLinkedIn:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/connektusjobs"));
                startActivity(intent);
                break;
            case R.id.ivTwiter:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ConnektUsJobs"));
                startActivity(intent);
                break;
            case R.id.ivMail:
                sharOnEmail();
                break;
        }
    }

    private void sharOnEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: " + "info@connektus.com.au"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Enter something");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hii android !");
        startActivity(Intent.createChooser(emailIntent, "Send feedback"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fileList();
    }
}
