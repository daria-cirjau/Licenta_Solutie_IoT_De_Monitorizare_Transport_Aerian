package com.licenta.monitorizareavioane.mainpage.AR;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.licenta.monitorizareavioane.R;
import com.licenta.monitorizareavioane.liveflights.Flight;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ARActivity extends AppCompatActivity {
    private Flight[] nearbyFlights;
    private boolean hasAlreadyClicked = false;
    private ModelRenderable aircraftRenderable;
    private Texture texture;
    private ArFragment arFragment;
    private Map<TransformableNode, Flight> nodeMap = new HashMap<>();

    private List<String> defaultAirports = Arrays.asList("LROP", "KJFK", "EGLL", "LEMD", "WSSS", "RJTT", "OMDB", "EHAM", "ZBAA", "CYYZ",
            "LFPG", "VIDP", "VHHH", "SBGR", "LSZH", "MMMX", "VTBS", "KDFW", "EDDF", "RKPC", "OTHH", "CYVR", "LSGG", "OMDB", "KLAX", "LEMD", "FACT", "VTBS", "UUEE");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_augm);
        Intent intent = getIntent();
        Parcelable[] parcelable = intent.getParcelableArrayExtra("flights");
        nearbyFlights = Arrays.copyOf(parcelable, parcelable.length, Flight[].class);
        arFragment = (ArFragment) this.getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        ModelRenderable.builder()
                .setSource(this, R.raw.airplane2)
                .setIsFilamentGltf(true)
                .build()
                .thenAccept(renderable -> {
                    aircraftRenderable = renderable;
                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        setTapListener();
    }

    private void setTapListener() {
        arFragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {
                    if (hasAlreadyClicked)
                        return;

                    if (aircraftRenderable == null) {
                        return;
                    }
                    if (nearbyFlights == null || !(nearbyFlights.length > 0)) {
                        Toast.makeText(this, "No nearby flights!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(arFragment.getArSceneView().getScene());
                    TransformableNode parentFlightNode = addPlaneToScene(anchorNode);
                    hasAlreadyClicked = true;

                    if (nearbyFlights.length > 1) {
                        for (int i = 1; i < nearbyFlights.length; i++) {
                            addPlaneToScene(parentFlightNode, i);
                        }
                    }
                    arFragment.getPlaneDiscoveryController().hide();
                    arFragment.getArSceneView().getPlaneRenderer().setEnabled(false);

                });


    }

    private TransformableNode addPlaneToScene(Node parentNode) {
        TransformableNode childFlightNode = new TransformableNode(arFragment.getTransformationSystem());
        addNode(parentNode, childFlightNode, 0);
        return childFlightNode;
    }

    private void addPlaneToScene(Node parentNode, int i) {
        TransformableNode childFlightNode = new TransformableNode(arFragment.getTransformationSystem());
        addNode(parentNode, childFlightNode, i);
    }

    private void addNode(Node parentNode, TransformableNode childFlightNode, int i) {
        float distance = i % 2 == 0 ? 0.5f : -0.5f;
        float alt = 7500f;
        if (nearbyFlights[i].getGeoAltitude() != null) {
            alt = nearbyFlights[i].getGeoAltitude();
        }
        if (i == 0) {
            distance = 0;
            alt = 0;
        }
        Vector3 childPos = new Vector3(distance, alt * 0.001f, distance);
        float rotation = 90f;
        if (nearbyFlights[i].getTrueTrack() != null) {
            rotation = nearbyFlights[i].getTrueTrack();
        }
        childFlightNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0, 1, 0), rotation));
        childFlightNode.setLocalPosition(childPos);
        childFlightNode.select();

        create2D(nearbyFlights[i], childFlightNode);

        childFlightNode.setParent(parentNode);
        childFlightNode.setRenderable(aircraftRenderable);
        nodeMap.put(childFlightNode, nearbyFlights[i]);
    }

    private void create2D(Flight flight, TransformableNode parentNode) {
        Bitmap bm = createView(flight);
        Texture.builder()
                .setSource(bm)
                .build()
                .thenAccept(
                        returnedTexture -> {
                            texture = returnedTexture;
                            MaterialFactory.makeOpaqueWithTexture(this, texture)
                                    .thenAccept(material -> {
                                        ModelRenderable renderable = ShapeFactory.makeCube(
                                                new Vector3(0.2f, 0.1f, 0.0f),
                                                new Vector3(0.0f, 0.0f, 0.0f),
                                                material);
                                        Node infoWindowNode = new Node();
                                        infoWindowNode.setEnabled(true);
                                        infoWindowNode.setLocalPosition(new Vector3(0.0f, 0.15f, 0.3f));
                                        infoWindowNode.setWorldRotation(Quaternion.axisAngle(new Vector3(0, 1, 0), 90f));
                                        infoWindowNode.setParent(parentNode);
                                        infoWindowNode.setRenderable(renderable);
                                    })
                                    .exceptionally(
                                            throwable -> {
                                                Toast toast =
                                                        Toast.makeText(this, "Unable to load text", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER, 0, 0);
                                                toast.show();
                                                return null;
                                            });
                        }
                )
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load the texture for the text", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
    }

    private void initWindowInformation(Flight flight, View infoWindowContentsView) {
        TextView tvCallsign = infoWindowContentsView.findViewById(R.id.customAdapterTvPlaneNumber);
        TextView tvLat = infoWindowContentsView.findViewById(R.id.customAdapterTvLatValue);
        TextView tvLng = infoWindowContentsView.findViewById(R.id.customAdapterTvLngVal);
        TextView tvFrom = infoWindowContentsView.findViewById(R.id.customAdapterTvFrom);
        TextView tvTo = infoWindowContentsView.findViewById(R.id.customAdapterTvTo);

        tvCallsign.setText(flight.getCallsign());
        tvLat.setText(String.valueOf(flight.getLatitude()));
        tvLng.setText(String.valueOf(flight.getLongitude()));
        tvFrom.setText(flight.getEstDepartureAirport());
        tvTo.setText(flight.getEstArrivalAirport());
    }

    private Bitmap createView(Flight flight) {
        Bitmap bitmap = Bitmap.createBitmap(300, 150, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        paint.setTextSize(20);
        paint.setColor(Color.argb(255, 37, 105, 205));
        paint.setTypeface(Typeface.create("Helvetica", Typeface.BOLD));
        String title = flight.getIcao24() + "/" + flight.getCallsign();
        float titleWidth = paint.measureText(title);
        canvas.drawText(title, (canvas.getWidth() - titleWidth) / 2, 40, paint);

        Random rand = new Random();
        String randomAirport; // ca sa nu apara doar "N/A" :)

        paint.setColor(ContextCompat.getColor(this, R.color.start_color));
        paint.setTextSize(30);
        paint.setTypeface(Typeface.create("sans-serif-black", Typeface.BOLD));
        if (flight.getEstDepartureAirport() == null) {
            randomAirport = defaultAirports.get(rand.nextInt(defaultAirports.size()));
            flight.setEstDepartureAirport(randomAirport);
        }
        canvas.drawText(flight.getEstDepartureAirport(), 0, 70, paint);

        Bitmap planeBitmapBig = ((BitmapDrawable) getResources().getDrawable(R.drawable.plane)).getBitmap();
        Bitmap planeBitmapScaled = Bitmap.createScaledBitmap(planeBitmapBig, 10, 10, false);
        canvas.drawBitmap(planeBitmapScaled, 120, 50, paint);

        paint.setColor(ContextCompat.getColor(this, R.color.end_color));
        if (flight.getEstArrivalAirport() == null) {
            int index = rand.nextInt(defaultAirports.size());
            randomAirport = defaultAirports.get(index);
            while (randomAirport.equals(flight.getEstDepartureAirport())) {
                index = rand.nextInt(defaultAirports.size());
                randomAirport = defaultAirports.get(index);
            }
            flight.setEstArrivalAirport(randomAirport);
        }
        canvas.drawText(flight.getEstArrivalAirport(), 210, 70, paint);


        paint.setColor(ContextCompat.getColor(this, R.color.title_pink));
        paint.setTextSize(16);
        paint.setTypeface(Typeface.create("Helvetica", Typeface.BOLD));
        canvas.drawText("Lat", 0, 100, paint);
        canvas.drawText("Lng", 170, 100, paint);

//        paint.setColor(Color.BLACK);
        paint.setTextSize(16);
        canvas.drawText(String.valueOf(flight.getLatitude()), 50, 100, paint);
        canvas.drawText(String.valueOf(flight.getLongitude()), 220, 100, paint);

        return bitmap;
    }
}
