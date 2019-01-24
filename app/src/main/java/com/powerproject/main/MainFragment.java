package com.powerproject.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.powerproject.login.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private int[] imageResId = {
            R.drawable.manager,
            R.drawable.member,
            R.drawable.owner
    };

    public MainFragment() {
        // Required empty public constructor
    }

    View view;
    Adapter adapter;

    private void setupViewPager(ViewPager viewPager) {
///Here we have to pass ChildFragmentManager instead of FragmentManager.
        adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new ProjectViewFragment(), "Manager");
        adapter.addFragment(new MemberViewFragment(), "Member");
        adapter.addFragment(new PoViewFragment(), "Owner");
        viewPager.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this, view);
        final ViewPager viewPager = ButterKnife.findById(view, R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = ButterKnife.findById(view, R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(imageResId[0]);
        tabLayout.getTabAt(1).setIcon(imageResId[1]);
        tabLayout.getTabAt(2).setIcon(imageResId[2]);

        FloatingActionButton fab = ButterKnife.findById(view, R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CreateProject.class));
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //ButterKnife.unbind(this);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}

/**
// Create the adapter that will return a fragment for each section
mPagerAdapter = new FragmentPagerAdapter(getActivity().getSupportFragmentManager()) {
    private final Fragment[] mFragments = new Fragment[] {
            new ProjectViewFragment(),
            new MemberViewFragment(),
            new PoViewFragment()
            //new MyTopPostsFragment(),
    };

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }
    @Override
    public int getCount() {
        return mFragments.length;
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentNames[position];
    }
};
// Set up the ViewPager with the sections adapter.
mViewPager = (ViewPager) rootView.findViewById(R.id.container);
//mViewPager.setAdapter(mPagerAdapter);
TabLayout tabLayout = (TabLayout)rootView.findViewById(R.id.tabs);
tabLayout.setupWithViewPager(mViewPager);
tabLayout.getTabAt(0).setIcon(imageResId[0]);
tabLayout.getTabAt(1).setIcon(imageResId[1]);
tabLayout.getTabAt(2).setIcon(imageResId[2]);


FloatingActionButton fab = (FloatingActionButton)rootView.findViewById(R.id.fab);
fab.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        startActivity(new Intent(getActivity(), CreateProject.class));
    }
});
return rootView;
}

@Override
public void onResume() {
mViewPager.setAdapter(mPagerAdapter);
//pager.setCurrentItem(activePage);
}
}
*/