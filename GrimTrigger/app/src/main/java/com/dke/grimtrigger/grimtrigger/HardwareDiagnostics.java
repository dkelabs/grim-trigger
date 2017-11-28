package com.dke.grimtrigger.grimtrigger;

import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Convert these to a strings file
public class HardwareDiagnostics extends ExpandableListActivity {
    String device = Build.MODEL;

    private ExpandableListAdapter mAdapterView;
    android.widget.ExpandableListView expandableListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.expandable_list);

        // Sets up top (group) level items
        List<Map<String, String>> groupListItem = new ArrayList<Map<String, String>>();

        // Second level child items
        List<List<Map<String, String>>> childListItem = new ArrayList<List<Map<String, String>>>();

        /* ******************** Group item 1 ********************* */
        Map<String, String> group1 = new HashMap<String, String>();
        groupListItem.add(group1);
        group1.put("parentItem", "IOIO-OTG - V2.2");

        List<Map<String, String>> children1 = new ArrayList<Map<String, String>>();
        Map<String, String> childrenitem1 = new HashMap<String, String>();
        children1.add(childrenitem1);
        childrenitem1.put("childItem", "This IOIO board is used to interface with the drone detector (as input) and jammer (as output).");

        Map<String, String> childrenitem2 = new HashMap<String, String>();
        children1.add(childrenitem2);
        childrenitem2.put("childItem", "https://www.sparkfun.com/products/13613?tag=dk3-20");

        childListItem.add(children1);

        /* ******************** Group Item 2  ********************* */
        Map<String, String> childrenitem6 = new HashMap<String, String>();
        groupListItem.add(childrenitem6);
        childrenitem6.put("parentItem", "Raspberry Pi 3");
        List<Map<String, String>> children2 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem7 = new HashMap<String, String>();
        children2.add(childrenitem7);
        childrenitem7.put("childItem", "The Pi 3 is necessary for running the wireless cracking functionality (and hosts the Alfa) in order to perform a successful takeover.");

        Map<String, String> childrenitem8 = new HashMap<String, String>();
        children2.add(childrenitem8);
        childrenitem8.put("childItem", "https://www.amazon.com/Raspberry-Pi-RASPBERRYPI3-MODB-1GB-Model-Motherboard/dp/B01CD5VC92/?tag=dk3-20");

        childListItem.add(children2);


        /* ******************** Group Item 3  ********************* */
        Map<String, String> childrenitem9 = new HashMap<String, String>();
        groupListItem.add(childrenitem9);
        childrenitem9.put("parentItem", "eSUN 1.75mm Black PLA PRO (PLA+) 3D Printer Filament 1KG Spool (2.2lbs), Black");
        List<Map<String, String>> children3 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem10 = new HashMap<String, String>();
        children3.add(childrenitem10);
        childrenitem10.put("childItem", "3D printing filament used for printing the \"Grimbox\" hardware assembly model.");

        Map<String, String> childrenitem11 = new HashMap<String, String>();
        children3.add(childrenitem11);
        childrenitem11.put("childItem", "https://www.amazon.com/gp/product/B01EKEMDA6/?tag=dk3-20");

        childListItem.add(children3);


        /* ******************** Group Item 4  ********************* */
        Map<String, String> childrenitem13 = new HashMap<String, String>();
        groupListItem.add(childrenitem13);
        childrenitem13.put("parentItem", "Plugable USB 2.0 4-Port High Speed Charging Hub with 12.5W Power Adapter");
        List<Map<String, String>> children4 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem14 = new HashMap<String, String>();
        children4.add(childrenitem14);
        childrenitem14.put("childItem", "A powered hub used to interface with the Jammer (e.g. IOIO board) without drawing power from the Android device.");

        Map<String, String> childrenitem15 = new HashMap<String, String>();
        children4.add(childrenitem15);
        childrenitem15.put("childItem", "https://www.amazon.com/gp/product/B005P2BY5I/?tag=dk3-20");

        childListItem.add(children4);


        /* ******************** Group Item 5  ********************* */
        Map<String, String> childrenitem17 = new HashMap<String, String>();
        groupListItem.add(childrenitem17);
        childrenitem17.put("parentItem", "Alfa AWUS036NH Wireless Network Adapter");
        List<Map<String, String>> children5 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem18 = new HashMap<String, String>();
        children5.add(childrenitem18);
        childrenitem18.put("childItem", "Capable of monitor mode and necessary for performing a drone takeover.");

        Map<String, String> childrenitem19 = new HashMap<String, String>();
        children5.add(childrenitem19);
        childrenitem19.put("childItem", "https://www.amazon.com/Alfa-AWUS036NH-802-11g-Wireless-Long-Range/dp/B003YIFHJY/?tag=dk3-20");

        childListItem.add(children5);


        /* ******************** Group Item 6  ********************* */
        Map<String, String> childrenitem20 = new HashMap<String, String>();
        groupListItem.add(childrenitem20);
        childrenitem20.put("parentItem", "RAVPower 20100 Power Bank");
        List<Map<String, String>> children6 = new ArrayList<Map<String, String>>();

        Map<String, String> childrenitem21 = new HashMap<String, String>();
        children6.add(childrenitem21);
        childrenitem21.put("childItem", "Used to power all hardware. Also serves as a data hub, if desired.");

        Map<String, String> childrenitem22 = new HashMap<String, String>();
        children6.add(childrenitem22);
        childrenitem22.put("childItem", "https://www.amazon.com/Portable-RAVPower-20100mAh-Transfer-Smartphones/dp/B0156HCJQO/?tag=dk3-20");

        childListItem.add(children6);

        /**
         * The SimpleExpandableListAdapter fetches data from Lists of Maps
         *
         * @param Context context
         * @param List<? extends Map<String, ?>> groupData
         * @param int groupLayout
         * @param String[] groupFrom
         * @param int[] groupTo
         * @param List<? extends List<? extends Map<String, ?>>> childData
         * @param int childLayout
         * @param String[] childFrom
         * @param int[] childTo
         */
        mAdapterView = new SimpleExpandableListAdapter(
                this,
                groupListItem,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{"parentItem"},
                new int[]{android.R.id.text1, android.R.id.text2},
                childListItem,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{"childItem"},
                new int[]{android.R.id.text1}
        );

        setListAdapter(mAdapterView);
        expandableListView = getExpandableListView();
        expandableListView.setOnChildClickListener(new android.widget.ExpandableListView.OnChildClickListener() {
            /**
             * Click handler of any of the child elements
             *
             * @param parent
             * @param v
             * @param groupPosition
             * @param childPosition
             * @param id
             * @return boolean
             */
            @Override
            public boolean onChildClick(android.widget.ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                switch (groupPosition) {
                    case 0:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "ListView Example",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "ListView Tutorial",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 1:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "Android ListView",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "Expandable ListView",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 2:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "Android ListView",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "Expandable ListView",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 3:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "Android ListView",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "Expandable ListView",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 4:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "Android ListView",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "Expandable ListView",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 5:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "Android ListView",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "Expandable ListView",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                    case 6:
                        switch (childPosition) {
                            case 0:
                                Toast.makeText(getBaseContext(), "Android ListView",
                                        Toast.LENGTH_LONG).show();
                                // Pull the layout params, set just the height
                                v.getLayoutParams().height = 400;
                                v.setLayoutParams(v.getLayoutParams());
                                break;
                            case 1:
                                Toast.makeText(getBaseContext(), "Expandable ListView",
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                        break;
                }
                return false;
            }
        });
    }
}
