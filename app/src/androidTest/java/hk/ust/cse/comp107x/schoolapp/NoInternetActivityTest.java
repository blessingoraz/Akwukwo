package hk.ust.cse.comp107x.schoolapp;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AndroidTestCase;
import android.widget.Button;

import com.robotium.solo.Solo;


/**
 * Created by blessingorazulume on 7/19/16.
 */
public class NoInternetActivityTest extends ActivityInstrumentationTestCase2<NoInternetActivity> {
    Context context;
    Solo solo;
    NoInternetActivity mActivity;

    public NoInternetActivityTest() {
        super(NoInternetActivity.class);
    }

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
        mActivity = getActivity();
        context = mActivity.getApplicationContext();
    }

    public void testTryAgain() throws Exception {
        solo.clickOnButton("Try Again");
        assertTrue(solo.searchText("loading"));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }
}
