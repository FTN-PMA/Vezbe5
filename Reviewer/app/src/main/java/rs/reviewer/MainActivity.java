package rs.reviewer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import model.NavItem;
import rs.reviewer.adapters.DrawerListAdapter;
import rs.reviewer.fragments.MyFragment;
import rs.reviewer.sync.SyncReceiver;
import rs.reviewer.sync.SyncService;
import rs.reviewer.tools.FragmentTransition;
import rs.reviewer.tools.ReviewerTools;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mDrawerPane;
    private CharSequence mTitle;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    //Sync stuff
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    private SyncReceiver sync;
    public static String SYNC_DATA = "SYNC_DATA";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        prepareMenu(mNavItems);
        
        mTitle = getTitle();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerList = (ListView) findViewById(R.id.navList);
        
        // Populate the Navigtion Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerList.setAdapter(adapter);

        // enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);
            actionBar.setHomeButtonEnabled(true);
        }

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
//                getActionBar().setTitle(mTitle);
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                getActionBar().setTitle(mDrawerTitle);
                getSupportActionBar().setTitle("iReviewer");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        if (savedInstanceState == null) {
            selectItemFromDrawer(0);
        }

        setUpReceiver();
    }

    private void setUpReceiver(){
        sync = new SyncReceiver();

        //definisemo manager i kazemo kada je potrebno da se ponavlja
        /*
        parametri:
            context: this - u komkontekstu zelimo da se intent izvrsava
            requestCode: 0 - nas jedinstev kod
            intent: intent koji zelimo da se izvrsi kada dodje vreme
            flags: 0 - flag koji opisuje sta da se radi sa intent-om kada se poziv desi
            detaljnije:https://developer.android.com/reference/android/app/PendingIntent.html#getService(android.content.Context, int, android.content.Intent, int)
        */
        Intent alarmIntent = new Intent(this, SyncService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);

        //koristicemo sistemski AlarmManager pa je potrebno da dobijemo
        //njegovu instancu.
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    }

    /*
     * Prilikom startovanja aplikacije potrebno je registrovati
     * elemente sa kojimaa radimo. Kada aplikaciaj nije aktivna
     * te elemente moramo da uklnimo.
     */
    @Override
    protected void onResume() {
    	super.onResume();

        //Za slucaj da referenca nije postavljena da se izbegne problem sa androidom!
        if (manager == null) {
            setUpReceiver();
        }

        /*
         * Kada zelimo da se odredjeni zadaci ponavljaju, potrebno je
         * da registrujemo manager koji ce motriti kada je vreme da se
         * taj posao obavi. Kada registruje vreme za pokretanje zadatka
         * on emituje Intent operativnom sistemu sta je potrebno da se izvrsi.
         * Takodje potrebno je da definisemo ponavljanja tj. na koliko
         * vremena zelimo da se posao ponovo obavi
         * */
        int interval = ReviewerTools.calculateTimeTillNextSync(1);

        //definisemo kako ce alarm manager da reaguje.
        //prvi parametar kaze da ce reagovati u rezimu ponavljanja
        //drugi parametar od kada krece da meri vreme
        //treci parametar na koliko jedinica vremena ce ragovati (minimalno 1min)
        //poslednji parametar nam govori koju akciju treba da preduzmemo kada se alarm iskljuci
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();

        /*
         * Registrujemo nas BroadcastReceiver i dodajemo mu 'filter'.
         * Filter koristimo prilikom prispeca poruka. Jedan receiver
         * moze da reaguje na vise tipova poruka. One nam kazu
         * sta tacno treba da se desi kada poruka odredjenog tipa (filera)
         * stigne.
         * */
        IntentFilter filter = new IntentFilter();
        filter.addAction(SYNC_DATA);
        registerReceiver(sync, filter);
    }
    
    private void prepareMenu(ArrayList<NavItem> mNavItems ){
    	mNavItems.add(new NavItem(getString(R.string.home), getString(R.string.home_long), R.drawable.ic_action_map));
        mNavItems.add(new NavItem(getString(R.string.places), getString(R.string.places_long), R.drawable.ic_action_place));
        mNavItems.add(new NavItem(getString(R.string.preferences), getString(R.string.preferences_long), R.drawable.ic_action_settings));
        mNavItems.add(new NavItem(getString(R.string.about), getString(R.string.about_long), R.drawable.ic_action_about));
        mNavItems.add(new NavItem(getString(R.string.sync_data), getString(R.string.sync_data_long), R.drawable.ic_action_refresh));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.activity_itemdetail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	selectItemFromDrawer(position);
        }
    }
    
    
    private void selectItemFromDrawer(int position) {
        if(position == 0){
            FragmentTransition.to(MyFragment.newInstance(), this, false);
        }else if(position == 1){
           //..
        }else if(position == 2){
        }else if(position == 3){
            //..
        }else if(position == 4){
            //..
        }else if(position == 5){
            //...
        }else{
            Log.e("DRAWER", "Nesto van opsega!");
        }

        mDrawerList.setItemChecked(position, true);
        if(position != 5) // za sve osim za sync
        {
            setTitle(mNavItems.get(position).getmTitle());
        }
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * Moramo voditi racuna o komponentama koje je potrebno osloboditi
     * kada aplikacija nije aktivna.
     * */
    @Override
    protected void onPause() {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }

        //osloboditi resurse
        if(sync != null){
            unregisterReceiver(sync);
        }

        super.onPause();

    }
}
