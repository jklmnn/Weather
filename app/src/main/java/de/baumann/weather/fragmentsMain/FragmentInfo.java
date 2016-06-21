package de.baumann.weather.fragmentsMain;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import de.baumann.weather.Browser;
import de.baumann.weather.R;
import de.baumann.weather.helper.BrowserDatabase;
import de.baumann.weather.helper.CustomListAdapter;


public class FragmentInfo extends Fragment {

    private ListView listView = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] itemname ={
                getString(R.string.action_thema),
                getString(R.string.action_lexikon),
                getString(R.string.action_karten),
                getString(R.string.action_radar),
                getString(R.string.action_satellit),
                getString(R.string.action_webSearch),
        };

        final String[] itemURL ={
                "http://www.dwd.de/SiteGlobals/Forms/ThemaDesTages/ThemaDesTages_Formular.html?pageNo=0&queryResultId=null",
                "http://www.dwd.de/DE/service/lexikon/lexikon_node.html",
                "https://www.meteoblue.com/de/wetter/karte/film/europa",
                "https://www.meteoblue.com/de/wetter/karte/niederschlag_1h/europa",
                "https://www.meteoblue.com/de/wetter/karte/satellit/europa",
                "https://startpage.com/",
        };

        Integer[] imgid={
                R.drawable.img1,
                R.drawable.img2,
                R.drawable.img3,
                R.drawable.img4,
                R.drawable.img5,
                R.drawable.img6,
        };

        View rootView = inflater.inflate(R.layout.fragment_bookmarks, container, false);

        setHasOptionsMenu(true);

        ImageView imgHeader = (ImageView) rootView.findViewById(R.id.imageView3);
        if(imgHeader != null) {
            TypedArray images = getResources().obtainTypedArray(R.array.splash_images);
            int choice = (int) (Math.random() * images.length());
            imgHeader.setImageResource(images.getResourceId(choice, R.drawable.splash1));
            images.recycle();
        }

        CustomListAdapter adapter=new CustomListAdapter(getActivity(), itemname, itemURL, imgid);
        listView = (ListView)rootView.findViewById(R.id.bookmarks);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Selecteditem= itemURL[+position];
                Intent intent = new Intent(getActivity(), Browser.class);
                intent.putExtra("url", Selecteditem);
                startActivityForResult(intent, 100);
                getActivity().finish();
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == Activity.RESULT_OK) {
                    if (data.getIntExtra("updated", 0) == 1) {
                        setBookmarkList();
                    }
                }
        }
    }

    private void setBookmarkList() {

        ArrayList<HashMap<String,String>> mapList = new ArrayList<>();

        try {
            BrowserDatabase db = new BrowserDatabase(getActivity());
            ArrayList<String[]> bookmarkList = new ArrayList<>();
            db.getBookmarks(bookmarkList);
            if (bookmarkList.size() == 0) {
                db.loadInitialData();
                db.getBookmarks(bookmarkList);
            }
            db.close();

            for (String[] strAry : bookmarkList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("seqno", strAry[0]);
                map.put("title", strAry[1]);
                map.put("url", strAry[2]);
                mapList.add(map);
            }

            SimpleAdapter simpleAdapter = new SimpleAdapter(
                    getActivity(),
                    mapList,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "url"},
                    new int[] {android.R.id.text1, android.R.id.text2}
            );

            listView.setAdapter(simpleAdapter);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}