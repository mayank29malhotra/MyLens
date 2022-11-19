package com.example.mylens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {
    private FirebaseFunctions mFunctions;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageLabeler labeler;
    // variables for our image view, image bitmap,
    // buttons, recycler view, adapter and array list.
    private ImageView img;
     Button snap, searchResultsBtn;
    private Bitmap imageBitmap;
    private RecyclerView resultRV;
    private SearchResultsRVAdapter searchResultsRVAdapter;
    private ArrayList<DataModal> dataModalArrayList;
    private String title, link, displayed_link, snippet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // initializing all our variables for views
        img = (ImageView) findViewById(R.id.capturedImage);
        snap = (Button) findViewById(R.id.snap);
        searchResultsBtn = findViewById(R.id.getresults);


        labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);


        // adding on click listener for our snap button.
        snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to capture an image.
                dispatchTakePictureIntent();

            }
        });
//
//         adding on click listener for our button to search data.


        searchResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calling a method to get search results.
try{
                resultRV = findViewById(R.id.dispsearchresults);
                getResults();
                // initializing our array list
                dataModalArrayList = new ArrayList<>();

                // initializing our adapter class.
                searchResultsRVAdapter = new SearchResultsRVAdapter(dataModalArrayList, Search.this);
                // layout manager for our recycler view.
                LinearLayoutManager manager = new LinearLayoutManager(Search.this, LinearLayoutManager.HORIZONTAL, false);

                // on below line we are setting layout manager
                // and adapter to our recycler view.
                resultRV.setLayoutManager(manager);
                resultRV.setAdapter(searchResultsRVAdapter);}
catch (Exception e)
{
    Toast.makeText(Search.this, e.toString(), Toast.LENGTH_SHORT).show();
}

            }
        });
    }






    private void getResults() {



        InputImage image = InputImage.fromBitmap(imageBitmap, 0);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        // Task completed successfully
                        // ...
                        String searchQuery = labels.get(0).getText();
                         searchData(searchQuery);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                        Toast.makeText(Search.this, "Fail to detect image..", Toast.LENGTH_SHORT).show();
                    }
                });
//        // inside the label image method we are calling a firebase vision image
//        // and passing our image bitmap to it.
//        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
//
//        // on below line we are creating a labeler for our image bitmap and
//        // creating a variable for our firebase vision image labeler.
//        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getOnDeviceImageLabeler();
//
//        // calling a method to process an image and adding on success listener method to it.
//        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
//            @Override
//            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
//                String searchQuery = firebaseVisionImageLabels.get(0).getText();
//                searchData(searchQuery);
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                // displaying error message.
//                Toast.makeText(Search.this, "Fail to detect image..", Toast.LENGTH_SHORT).show();
//            }
//        });
    }






    private void searchData(String searchQuery) {
        String apiKey = "bb63164567c688b904f1cfb9fea8b5e4425bc2004cdaece41067e870ed65ff4a";
        String url = "https://serpapi.com/search.json?q=" + searchQuery.trim() + "&location=Delhi,India&hl=en&gl=us&google_domain=google.com&api_key=" + apiKey;

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(Search.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // on below line we are extracting data from our json.
                    JSONArray organicResultsArray = response.getJSONArray("organic_results");
                    for (int i = 0; i < organicResultsArray.length(); i++) {
                        JSONObject organicObj = organicResultsArray.getJSONObject(i);
                        if (organicObj.has("title")) {
                            title = organicObj.getString("title");
                        }
                        if (organicObj.has("link")) {
                            link = organicObj.getString("link");
                        }
                        if (organicObj.has("displayed_link")) {
                            displayed_link = organicObj.getString("displayed_link");
                        }
                        if (organicObj.has("snippet")) {
                            snippet = organicObj.getString("snippet");
                        }
                        // on below line we are adding data to our array list.
                        dataModalArrayList.add(new DataModal(title, link, displayed_link, snippet));
                    }
                    // notifying our adapter class
                    // on data change in array list.
                    searchResultsRVAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // displaying error message.
                Toast.makeText(Search.this, "No Result found for the search query..", Toast.LENGTH_SHORT).show();
            }
        });
        // adding json object request to our queue.
        queue.add(jsonObjectRequest);
    }

    // method to capture image.
    private void dispatchTakePictureIntent() {
        // inside this method we are calling an implicit intent to capture an image.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // calling a start activity for result when image is captured.
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // inside on activity result method we are
        // setting our image to our image view from bitmap.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            // on below line we are setting our
            // bitmap to our image view.
            img.setImageBitmap(imageBitmap);
        }
    }
}
