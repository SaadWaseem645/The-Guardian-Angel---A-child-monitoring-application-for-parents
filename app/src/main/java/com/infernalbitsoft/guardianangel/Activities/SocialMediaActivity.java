package com.infernalbitsoft.guardianangel.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.infernalbitsoft.guardianangel.Fragments.MessengerFragment;
import com.infernalbitsoft.guardianangel.Fragments.WhatsAppFragment;
import com.infernalbitsoft.guardianangel.R;

public class SocialMediaActivity extends FragmentActivity {

    public ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private String[] tabName = {"Messenger","WhatsApp"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        TextView headerTitle = findViewById(R.id.header_title);
        headerTitle.setText(getString(R.string.option2));
        ImageView back = findViewById(R.id.header_back);
        back.setOnClickListener(v -> finish());

        viewPager = findViewById(R.id.social_media_pager);
        pagerAdapter = new SocialMediaTabsAdapter(this);

        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabName[position])).attach();

    }

    private static class SocialMediaTabsAdapter extends FragmentStateAdapter {

        Fragment[] fragments = {new MessengerFragment(), new WhatsAppFragment()};

        public SocialMediaTabsAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments[position];
        }

        @Override
        public int getItemCount() {
            return fragments.length;
        }
    }
}