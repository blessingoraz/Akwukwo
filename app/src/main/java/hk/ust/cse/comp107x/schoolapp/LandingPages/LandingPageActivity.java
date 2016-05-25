package hk.ust.cse.comp107x.schoolapp.LandingPages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import hk.ust.cse.comp107x.schoolapp.Fragments.FirstFragment;
import hk.ust.cse.comp107x.schoolapp.Fragments.SecondFragment;
import hk.ust.cse.comp107x.schoolapp.Fragments.ThirdFragment;
import hk.ust.cse.comp107x.schoolapp.R;

public class LandingPageActivity extends AppCompatActivity {

    private TextView mAlreadyMember;
    ImageView animatedImageImage;
    ViewPager viewPager;

    public void gettingStarted(View view) {

        startActivity(new Intent(LandingPageActivity.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing_page);

        animatedImageImage = (ImageView) findViewById(R.id.indicator);

        mAlreadyMember = (TextView) findViewById(R.id.already_a_memeber);

        mAlreadyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LandingPageActivity.this, LoginActivity.class));
            }
        });

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch(position) {
                    case 0:
                        switchToPostion(0);
                        break;
                    case 1:
                        switchToPostion(1);
                        break;
                    case 2:
                        switchToPostion(2);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {

            super(fm);
        }
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return FirstFragment.newInstance(0, "Hello");
                case 1:
                    return SecondFragment.newInstance(1, "Hmmm");
                case 2:
                    return ThirdFragment.newInstance(2, "Holla");
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

    int switchToPostion(int position){
        // declare images for the current postion...
        int[] animatedImage = new int[]{R.drawable.indicator1, R.drawable.indicator2, R.drawable.indicator3};

        //...and set the image appropriate for the current page
        animatedImageImage.setImageResource (animatedImage[position]);

        //return the current position of the page

        return position;
    }

}
