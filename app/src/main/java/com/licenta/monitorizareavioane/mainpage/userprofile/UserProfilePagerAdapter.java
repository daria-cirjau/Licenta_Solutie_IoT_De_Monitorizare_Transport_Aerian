package com.licenta.monitorizareavioane.mainpage.userprofile;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class UserProfilePagerAdapter extends FragmentPagerAdapter {
    UserProfilePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new UserProfileFragment();
            case 1:
                return new RewardsFragment();
            case 2:
                return new ChangePasswordFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "My profile";
            case 1:
                return "My rewards";
            case 2:
                return "Change password";
            default:
                return null;
        }
    }

}


