package com.alium.yoruba_quran.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import com.alium.yoruba_quran.R;
import com.alium.yoruba_quran.data.Chapter;
import com.alium.yoruba_quran.ui.chapterdetailsfragment.ChapterDetailsFragment;
import com.alium.yoruba_quran.ui.chapters.ChaptersFragment;
import com.alium.yoruba_quran.ui.favorites.FavoritesFragment;
import com.alium.yoruba_quran.ui.homefeed.SettingsFragment;
import com.alium.yoruba_quran.ui.main.MainActivity;
import com.alium.yoruba_quran.ui.main.MainContract;
import com.alium.yoruba_quran.ui.map.MapFragment;
import com.codemybrainsout.ratingdialog.RatingDialog;

import javax.inject.Inject;

/**
 * Created by Lucas on 02/01/2017.
 */

public class MainNavigator implements MainContract.Navigator {

    private static final String TAG_DETAILS = "tag_details";
    private static final String TAG_MASTER = "tag_master";
    private MainActivity mainActivity;

    public enum State {
        SINGLE_COLUMN_MASTER, SINGLE_COLUMN_DETAILS, TWO_COLUMNS_EMPTY, TWO_COLUMNS_WITH_DETAILS
    }

    @Inject
    public MainNavigator(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private boolean clearDetails() {
        final Fragment details = mainActivity.getSupportFragmentManager().findFragmentByTag(TAG_DETAILS);
        if (details != null) {
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .remove(details)
                    .commitNow();
            return true;
        }
        return false;
    }

    private void clearMaster() {
        Fragment master = mainActivity.getSupportFragmentManager().findFragmentByTag(TAG_MASTER);
        if (master != null) {
            mainActivity.getSupportFragmentManager().beginTransaction().remove(master).commitNow();
        }
    }

    @Override
    public void goToHomeFeed() {
        clearDetails();
        mainActivity.getCustomAppBar().setState(State.SINGLE_COLUMN_MASTER);
        mainActivity.getContainersLayout().setState(State.SINGLE_COLUMN_MASTER);
        SettingsFragment fragment = SettingsFragment.newInstance();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.activity_main__frame_master, fragment, TAG_MASTER).commitNow();
    }

    @Override
    public void goToPeople() {
        clearDetails();
        mainActivity.getCustomAppBar().setState(State.TWO_COLUMNS_EMPTY);
        mainActivity.getContainersLayout().setState(State.TWO_COLUMNS_EMPTY);
        ChaptersFragment master = ChaptersFragment.newInstance();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.activity_main__frame_master, master, TAG_MASTER).commitNow();
    }

    @Override
    public void goToFavorites() {
        clearDetails();
        mainActivity.getCustomAppBar().setState(State.SINGLE_COLUMN_MASTER);
        mainActivity.getContainersLayout().setState(State.SINGLE_COLUMN_MASTER);
        FavoritesFragment fragment = FavoritesFragment.newInstance();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.activity_main__frame_master, fragment, TAG_MASTER).commitNow();
    }

    @Override
    public void goToMap() {
        clearMaster();
        mainActivity.getCustomAppBar().setState(State.SINGLE_COLUMN_DETAILS);
        mainActivity.getContainersLayout().setState(State.SINGLE_COLUMN_DETAILS);
        MapFragment fragment = MapFragment.newInstance();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.activity_main__frame_details, fragment, TAG_DETAILS).commitNow();
    }

    @Override
    public void goToPersonDetails(Chapter chapter) {
        mainActivity.getCustomAppBar().setState(State.TWO_COLUMNS_WITH_DETAILS);
        mainActivity.getContainersLayout().setState(State.TWO_COLUMNS_WITH_DETAILS);
        ChapterDetailsFragment fragment = ChapterDetailsFragment.newInstance(chapter);
        mainActivity.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.activity_main__frame_details, fragment, TAG_DETAILS)
                .commitNow();
    }

    @Override
    public void goToSettings() {
        //start new activity
        clearDetails();
        mainActivity.getCustomAppBar().setState(State.SINGLE_COLUMN_MASTER);
        mainActivity.getContainersLayout().setState(State.SINGLE_COLUMN_MASTER);
        SettingsFragment fragment = SettingsFragment.newInstance();
        mainActivity.getSupportFragmentManager().beginTransaction().replace(R.id.activity_main__frame_master, fragment, TAG_MASTER).commitNow();
    }

    @Override
    public void goToFeedback() {
        //start new activity
        showFeedBackDialog();
    }

    private void showFeedBackDialog() {
        Toast.makeText(mainActivity, "Supposed to show diag", Toast.LENGTH_SHORT).show();

        final RatingDialog ratingDialog = new RatingDialog.Builder(mainActivity)
                .icon(mainActivity.getResources().getDrawable(R.drawable.ic_verse_bg))
                .threshold(3)
                .title("How was your experience with us?")
                .titleTextColor(R.color.black)
                .positiveButtonTextColor(R.color.white)
                .negativeButtonTextColor(R.color.grey_500)
                .formTitle("Submit Feedback")
                .formHint("Tell us where we can improve")
                .formSubmitText("Submit")
                .formCancelText("Cancel")
                .ratingBarColor(R.color.colorAccent)
                .positiveButtonBackgroundColor(R.color.colorAccent)
                .negativeButtonBackgroundColor(R.color.grey)
                .onRatingChanged(new RatingDialog.Builder.RatingDialogListener() {
                    @Override
                    public void onRatingSelected(float rating, boolean thresholdCleared) {

                    }
                })
                .onRatingBarFormSumbit(new RatingDialog.Builder.RatingDialogFormListener() {
                    @Override
                    public void onFormSubmitted(String feedback) {
                        //TODO Send email to me
                    }
                }).build();

        ratingDialog.show();
    }


    @Override
    public boolean onBackPressed() {
        State state = mainActivity.getContainersLayout().getState();
        if (state.equals(State.TWO_COLUMNS_WITH_DETAILS) && !mainActivity.getContainersLayout().hasTwoColumns()) {
            if (clearDetails()) {
                mainActivity.getContainersLayout().setState(State.TWO_COLUMNS_EMPTY);
                return true;
            }
        }
        return false;
    }
}
