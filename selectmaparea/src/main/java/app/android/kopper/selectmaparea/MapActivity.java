package app.android.kopper.selectmaparea;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import app.android.kopper.selectmaparea.util.LogUtil;

/**
 * Created by kopper on 2015-02-22.
 * (C) Copyright 2015 kopperek@gmail.com
 * <p/>
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

public class MapActivity extends FragmentActivity {

    public static final String SELECTED_POSITIONS="selected.positions";
    private static final int WIZARD_RESULT_CODE=0x01;

    LinkedList<Marker> markers=new LinkedList<>();
    private Polygon polygon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);
            SupportMapFragment mapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
            final GoogleMap map=mapFragment.getMap();
            //todo: map is null when no internet connection ??
            if(map!=null) {
                map.getUiSettings().setCompassEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.getUiSettings().setIndoorLevelPickerEnabled(false);
                map.getUiSettings().setRotateGesturesEnabled(false);

                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        createMarker(latLng,map);
                    }
                });
                findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinkedList<LatLng> positions=getPositions();
                        if(positions.size()!=2)
                            Toast.makeText(getApplicationContext(),getString(R.string.error_two_points),Toast.LENGTH_SHORT).show();
                        else {
                            Intent data=getOrCreateIntent();
                            data.putExtra(SELECTED_POSITIONS,positions);
                            Serializable nextPage=data.getSerializableExtra(MapActivity.class.getCanonicalName()+"-next");
                            if(nextPage!=null) {
                                try {
                                    Intent ii=new Intent(getApplicationContext(),(Class<?>)nextPage);
                                    ii.putExtras(data);
                                    startActivityForResult(ii,WIZARD_RESULT_CODE);
                                } catch(Exception e) {
                                    LogUtil.e(e);
                                }
                            } else {
                                setResult(RESULT_OK,data);
                                finish();
                            }
                        }
                    }
                });
                LinkedList<LatLng> positions=(LinkedList<LatLng>)getLastCustomNonConfigurationInstance();
                if(positions!=null) {
                    for(int a=0;a<positions.size();a++) {
                        createMarker(positions.get(a),map);
                    }
                } else {
                    Intent intent=getOrCreateIntent();
                    Bundle extras=intent.getExtras();
                    List<LatLng> selectedPoints=(List<LatLng>)extras.get(SELECTED_POSITIONS);
                    if(selectedPoints!=null)
                        for(LatLng pos:selectedPoints)
                            createMarker(pos,map);
                    map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition arg0) {
                            map.setOnCameraChangeListener(null);
                            if(markers.size()==2)
                                map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(markers.get(0).getPosition(),markers.get(1).getPosition()),40));
                            if(markers.size()==1)
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(markers.get(0).getPosition(),10));
                        }
                    });
                }
            } else { //no map
                ((TextView)findViewById(R.id.message)).setText(getString(R.string.error_map_load));
            }
        } catch(Exception e) {
            LogUtil.e(e);
        }
    }

    private void createMarker(LatLng latLng,GoogleMap map) {
        if(polygon!=null) {
            polygon.remove();
            polygon=null;
        }
        markers.add(map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cross)).anchor((float)0.5,(float)0.5)));
        if(markers.size()>2)
            markers.remove(0).remove();
        if(markers.size()==2) {

            List<LatLng> points=new LinkedList<>();
            points.add(markers.get(0).getPosition());
            points.add(new LatLng(markers.get(0).getPosition().latitude,markers.get(1).getPosition().longitude));
            points.add(markers.get(1).getPosition());
            points.add(new LatLng(markers.get(1).getPosition().latitude,markers.get(0).getPosition().longitude));
            polygon=map.addPolygon(new PolygonOptions().add(points.toArray(new LatLng[4])).strokeWidth((float)3.0).strokeColor(0xff404040).fillColor(0x20000000));
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return getPositions();
    }

    private LinkedList<LatLng> getPositions() {
        LinkedList<LatLng> positions=new LinkedList<LatLng>();
        for(int a=0;a<markers.size();a++)
            positions.add(markers.get(a).getPosition());
        return positions;
    }

    public Intent getOrCreateIntent() {
        return getIntent()==null?new Intent():getIntent();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if(resultCode==RESULT_OK) {
            setResult(RESULT_OK,data);
            finish();
        }
    }
}
