package com.example.surface.hotelapp;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

// W. Nov 24

public class AdminPage_Activity extends AppCompatActivity {

    // Declaring references to database entities
    private DatabaseReference dbRefUsers;
    private DatabaseReference dbRefActivities;
    private DatabaseReference dbRefBookings;
    private DatabaseReference dbRefOrders;
    private DatabaseReference dbRefRoomReview;

    // Used for holding the value of uniqueId of user information about should be found
    private String key;

    // Used for holding the value of uniqueId of user information about should be updated
    private String key1 = "";

    // Used for checking if data is found and displaying messages to user based of search results
    private boolean found;
    private boolean found1;
    private boolean found2 = false;

    // Declaring user inputs
    EditText name, email, firstName, lastName, region;
    RadioButton rdbTable1, rdbTable2, rdbTable3, rdbTable4, rdbTable5;
    Button show, find, update, delete, next, save;
    RadioGroup radioGroup;
    TextView resultText;

    @SuppressLint({"SourceLockedOrientationActivity", "CutPasteId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Blocks orientation change
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        setContentView(R.layout.activity_admin_page_);

        // Initializing the references
        // Connect with database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        dbRefUsers = mFirebaseDatabase.getReference("users");
        dbRefActivities = mFirebaseDatabase.getReference("activities");
        dbRefBookings = mFirebaseDatabase.getReference("bookings");
        dbRefOrders = mFirebaseDatabase.getReference("orders");
        dbRefRoomReview = mFirebaseDatabase.getReference("room_review");

        // Initializing the user inputs
        email = (EditText) findViewById(R.id.etEmail);
        firstName = (EditText) findViewById(R.id.etFirstName);
        lastName = (EditText) findViewById(R.id.etLastName);
        region = (EditText) findViewById(R.id.etRegion);
        radioGroup = (RadioGroup) findViewById(R.id.rbGroup);
        rdbTable1 = (RadioButton) findViewById(R.id.rdbTable1);
        rdbTable2 = (RadioButton) findViewById(R.id.rdbTable2);
        rdbTable3 = (RadioButton) findViewById(R.id.rdbTable3);
        rdbTable4 = (RadioButton) findViewById(R.id.rdbTable2);
        rdbTable5 = (RadioButton) findViewById(R.id.rdbTable3);
        find = (Button) findViewById(R.id.btnFind);
        update = (Button) findViewById(R.id.btnUpdate);
        delete = (Button) findViewById(R.id.btnDelete);
        show = (Button) findViewById(R.id.btnShow);
        save = (Button) findViewById(R.id.btnSave);
        next = (Button) findViewById(R.id.btnNext);
        resultText = (TextView) findViewById(R.id.text);

        // Set properties of inputs to make the page adaptive
        radioGroup.setVisibility(View.GONE);
        email.setVisibility(View.GONE);
        next.setVisibility(View.GONE);
        resultText.setVisibility(View.VISIBLE);
        save.setText("Close");

        // Try catch for preventing crashes
        try
        {
            // Method for displaying data in database
            show.setOnClickListener(view -> {

                email.setVisibility(View.GONE);
                resultText.setVisibility(View.GONE);
                next.setVisibility(View.GONE);

                firstName.setVisibility(View.GONE);
                lastName.setVisibility(View.GONE);
                region.setVisibility(View.GONE);

                radioGroup.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                save.setVisibility(View.VISIBLE);
                next.setText("Display");
                save.setText("Close");

                next.setOnClickListener(view1 -> {
                    resultText.setVisibility(View.VISIBLE);

                    // Check selected radio button
                    int radioButtonID = radioGroup.getCheckedRadioButtonId();
                    RadioButton radioButton = (RadioButton) radioGroup.findViewById(radioButtonID);
                    String selectedTable = (String) radioButton.getText();

                    // Depends on the radio button selected different requests to database happen
                    switch (selectedTable)
                    {
                        case "Users":
                            // Listening events for the user`s table
                            dbRefUsers.addValueEventListener(new ValueEventListener() {
                                @Override

                                // Method for checking if data changing
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int i = 1;
                                    resultText.setText("");

                                    // Count of users in database
                                    String count = String.valueOf(dataSnapshot.getChildrenCount());
                                    resultText.append("There are " + count + " users in database: " + '\n' + '\n');

                                    // Foreach loop for every user
                                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                    {
                                        // Display information of each user found
                                        resultText.append(
                                                "User #" + i + '\n'
                                                        + "Email: " + Objects.requireNonNull(postSnapshot.child("email").getValue())
                                                        + '\n' + "First name: " + Objects.requireNonNull(postSnapshot.child("firstName").getValue())
                                                        + '\n' + "Last name: " + Objects.requireNonNull(postSnapshot.child("lastName").getValue())
                                                        + '\n' + "Region: " + Objects.requireNonNull(postSnapshot.child("region").getValue())
                                                        + '\n' + "Key: " + postSnapshot.getKey()
                                                        + '\n' + '\n');

                                        // Used for numbering the users
                                        i++;
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        case "Activities":
                            dbRefActivities.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    resultText.setText("");
                                    resultText.append("List of booked activities in database: " + '\n' + '\n');

                                    // Foreach loop for each user in database
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                    {
                                        // Foreach loop for each user activity in database
                                        for (DataSnapshot postSnapshot1: postSnapshot.getChildren())
                                        {
                                            resultText.append(
                                                    "Activity: " + Objects.requireNonNull(postSnapshot1.child("activity").getValue())
                                                            + '\n' + "Date Of Purcahse: " + Objects.requireNonNull(postSnapshot1.child("dateOfPurchase").getValue())
                                                            + '\n' + "Price: " + Objects.requireNonNull(postSnapshot1.child("price").getValue())
                                                            + '\n' + "Quantity: " + Objects.requireNonNull(postSnapshot1.child("quantity").getValue())
                                                            + '\n' + "UserID: " + Objects.requireNonNull(postSnapshot1.child("userID").getValue())
                                                            + '\n' + '\n');
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        case "Bookings":
                            dbRefBookings.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    resultText.setText("");
                                    resultText.append("List of bookings in database: " + '\n' + '\n');
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                    {
                                        for (DataSnapshot postSnapshot1: postSnapshot.getChildren())
                                        {
                                            resultText.append(
                                                    "Date: " + Objects.requireNonNull(postSnapshot1.child("date").getValue())
                                                            + '\n' + "Duration: " + Objects.requireNonNull(postSnapshot1.child("duration").getValue())
                                                            + '\n' + "Number Of Guests: " + Objects.requireNonNull(postSnapshot1.child("numberOfGuests").getValue())
                                                            + '\n' + "Price of Booking: " + Objects.requireNonNull(postSnapshot1.child("priceOfBooking").getValue())
                                                            + '\n' + "Type: " + Objects.requireNonNull(postSnapshot1.child("type").getValue())
                                                            + '\n' + "UserID: " + Objects.requireNonNull(postSnapshot1.child("userID").getValue())
                                                            + '\n' + '\n');
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        case "Orders":
                            dbRefOrders.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    resultText.setText("");
                                    resultText.append("List of orders in database: " + '\n' + '\n');
                                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                                    {
                                        for (DataSnapshot postSnapshot1: postSnapshot.getChildren())
                                        {
                                            resultText.append(
                                                    "Date: " + Objects.requireNonNull(postSnapshot1.child("date").getValue())
                                                            + '\n' + "Item: " + Objects.requireNonNull(postSnapshot1.child("item").getValue())
                                                            + '\n' + "Price: " + Objects.requireNonNull(postSnapshot1.child("price").getValue())
                                                            + '\n' + "Quantity: " + Objects.requireNonNull(postSnapshot1.child("quantity").getValue())
                                                            + '\n' + "Total Price" + Objects.requireNonNull(postSnapshot1.child("totalPrice").getValue())
                                                            + '\n' + "UserID: " + postSnapshot.getKey()
                                                            + '\n' + '\n');
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;

                        case "Reviews":
                            dbRefRoomReview.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    resultText.setText("");
                                    resultText.append("List of reviews in database: " + '\n' + '\n');
                                    for (DataSnapshot postSnapshot1: dataSnapshot.getChildren())
                                    {
                                        resultText.append(
                                                "Rank: " + Objects.requireNonNull(postSnapshot1.child("rank").getValue())
                                                        + '\n' + "Review: " + Objects.requireNonNull(postSnapshot1.child("review").getValue())
                                                        + '\n' + "UserID: " + Objects.requireNonNull(postSnapshot1.child("userID").getValue())
                                                        + '\n' + '\n');
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError
                                                                databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                            break;
                    }
                });

                save.setOnClickListener(view12 -> {
                    next.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);
                    radioGroup.setVisibility(View.GONE);
                    resultText.setVisibility(View.GONE);

                });
            });

            // Method for searching all user`s data in database
            find.setOnClickListener(view -> {
                radioGroup.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                email.setText("");
                email.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                found1 = false;

                next.setText("Find");
                resultText.setText("");

                firstName.setVisibility(View.GONE);
                lastName.setVisibility(View.GONE);
                region.setVisibility(View.GONE);

                next.setOnClickListener(view15 -> {
                    key = "";
                    found1 = false;
                    resultText.setText("");

                    // Listening events in users table
                    dbRefUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // foreach loop in users table
                            for(DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                // Check if snapshot of data related to user`s email
                                if(Objects.requireNonNull(postSnapshot.child("email").getValue()).toString().equals(email.getText().toString()))
                                {
                                    found1 = true;
                                    resultText.setText("");
                                    resultText.append(
                                            "Email: " + Objects.requireNonNull(postSnapshot.child("email").getValue())
                                                    + '\n' + "First name: " + Objects.requireNonNull(postSnapshot.child("firstName").getValue())
                                                    + '\n' + "Last name: " + Objects.requireNonNull(postSnapshot.child("lastName").getValue())
                                                    + '\n' + "Region: " + Objects.requireNonNull(postSnapshot.child("region").getValue())
                                                    + '\n' + "Key: " + postSnapshot.getKey()
                                                    + '\n' + '\n');

                                    // Save user key from users table for future searches in other entities
                                    key = postSnapshot.getKey();
                                }
                            }

                            // Display results if user`s email wasn`t found in users entity
                            if(!found1)
                            {
                                resultText.setVisibility(View.VISIBLE);
                                resultText.setText("User " + email.getText().toString() + " is not found");
                                // Toast.makeText(getApplicationContext(), "User " + email.getText().toString() + " is not found", Toast.LENGTH_SHORT).show();
                            }
                        }


                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Searching rest of the tables by key which was saved from users table
                    dbRefActivities.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                for (DataSnapshot postSnapshot1: postSnapshot.getChildren())
                                {
                                    if (Objects.requireNonNull(postSnapshot1.child("userID").getValue()).toString().equals(key))
                                    {
                                        resultText.append("User " + email.getText().toString() + " booked: " + '\n' + '\n');
                                        resultText.append(
                                                "Activity: " + Objects.requireNonNull(postSnapshot1.child("activity").getValue())
                                                        + '\n' + "Date Of Purchase: " + Objects.requireNonNull(postSnapshot1.child("dateOfPurchase").getValue())
                                                        + '\n' + "Price: " + Objects.requireNonNull(postSnapshot1.child("price").getValue())
                                                        + '\n' + "Quantity: " + Objects.requireNonNull(postSnapshot1.child("quantity").getValue())
                                                        + '\n' + '\n');
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dbRefBookings.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                for (DataSnapshot postSnapshot1: postSnapshot.getChildren())
                                {
                                    if (Objects.requireNonNull(postSnapshot1.child("userID").getValue()).toString().equals(key))
                                    {
                                        resultText.append("User " + email.getText().toString() + " booked: " + '\n' + '\n');
                                        resultText.append(
                                                "Date: " + Objects.requireNonNull(postSnapshot1.child("date").getValue())
                                                        + '\n' + "Duration: " + Objects.requireNonNull(postSnapshot1.child("duration").getValue())
                                                        + '\n' + "Number Of Guests: " + Objects.requireNonNull(postSnapshot1.child("numberOfGuests").getValue())
                                                        + '\n' + "Price of Booking: " + Objects.requireNonNull(postSnapshot1.child("priceOfBooking").getValue())
                                                        + '\n' + "Type: " + Objects.requireNonNull(postSnapshot1.child("type").getValue())
                                                        + '\n' + '\n');
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dbRefOrders.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                for (DataSnapshot postSnapshot1: postSnapshot.getChildren())
                                {
                                    if (Objects.equals(postSnapshot.getKey(), key))
                                    {
                                        resultText.append("User " + email.getText().toString() + " ordered: " + '\n' + '\n');
                                        resultText.append(
                                                "Date: " + Objects.requireNonNull(postSnapshot1.child("date").getValue())
                                                        + '\n' + "Item: " + Objects.requireNonNull(postSnapshot1.child("item").getValue())
                                                        + '\n' + "Price: " + Objects.requireNonNull(postSnapshot1.child("price").getValue())
                                                        + '\n' + "Quantity: " + Objects.requireNonNull(postSnapshot1.child("quantity").getValue())
                                                        + '\n' + "Total Order:" + Objects.requireNonNull(postSnapshot1.child("totalPrice").getValue())
                                                        + '\n' + '\n');
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dbRefRoomReview.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                            {
                                if (Objects.requireNonNull(postSnapshot.child("userID").getValue()).toString().equals(key) || Objects.requireNonNull(dataSnapshot.getKey()).equals(key))
                                {
                                    resultText.append("User " + email.getText().toString() + " left review: " + '\n' + '\n');
                                    resultText.append(
                                            "Rank: " + Objects.requireNonNull(postSnapshot.child("rank").getValue())
                                                    + '\n' + "Review: " + Objects.requireNonNull(postSnapshot.child("review").getValue())
                                                    + '\n' + '\n');
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });

            // Method for update user`s information in users table
            update.setOnClickListener(view -> {
                resultText.setVisibility(View.GONE);
                radioGroup.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                save.setText("Update");
                next.setText("Find");
                email.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);

                found = false;

                firstName.setText("");
                lastName.setText("");
                region.setText("");

                firstName.setVisibility(View.GONE);
                lastName.setVisibility(View.GONE);
                region.setVisibility(View.GONE);



                next.setOnClickListener(view16 -> {
                    email.setVisibility(View.VISIBLE);
                    next.setVisibility(View.GONE);
                    save.setText("Find");

                    dbRefUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            // Try catch for handling exceptions
                            try
                            {

                                // Foreach loop for searching user by email in user`s table
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                {
                                    if (Objects.requireNonNull(postSnapshot.child("email").getValue()).toString().equals(email.getText().toString()))
                                    {
                                        found = true;

                                        // Display editText fields with current information about user
                                        firstName.setVisibility(View.VISIBLE);
                                        lastName.setVisibility(View.VISIBLE);
                                        region.setVisibility(View.VISIBLE);
                                        save.setVisibility(View.VISIBLE);
                                        save.setText("Update");
                                        resultText.setText("");

                                        firstName.setText(Objects.requireNonNull(postSnapshot.child("firstName").getValue()).toString());
                                        lastName.setText(Objects.requireNonNull(postSnapshot.child("lastName").getValue()).toString());
                                        region.setText(Objects.requireNonNull(postSnapshot.child("region").getValue()).toString());
                                        key1 = postSnapshot.getKey();
                                    }
                                }
                                if(!found)
                                {
                                    resultText.setVisibility(View.VISIBLE);
                                    resultText.setText("User " + email.getText().toString() + " is not found");
                                    next.setVisibility(View.VISIBLE);
                                }
                            }
                            catch (Exception ex){ex.printStackTrace();}
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Update method
                    save.setOnClickListener(view14 -> {

                        // Setting new values which were taken from three editText fields to the user object which is found by key1 from search part of update method
                        dbRefUsers.child(key1).child("firstName").setValue(firstName.getText().toString());
                        dbRefUsers.child(key1).child("lastName").setValue(lastName.getText().toString());
                        dbRefUsers.child(key1).child("region").setValue(region.getText().toString());

                        resultText.setText("");
                        Toast confirmToast = Toast.makeText(AdminPage_Activity.this, "Updated", Toast.LENGTH_SHORT);
                        confirmToast.show();
                        found = true;

                        save.setVisibility(View.GONE);
                        next.setVisibility(View.GONE);

                        email.setText("");
                        firstName.setText("");
                        lastName.setText("");
                        region.setText("");

                        email.setVisibility(View.GONE);
                        firstName.setVisibility(View.GONE);
                        lastName.setVisibility(View.GONE);
                        region.setVisibility(View.GONE);
                    });
                });
            });

            // Method for deleting user
            delete.setOnClickListener(view -> {
                resultText.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                radioGroup.setVisibility(View.GONE);
                email.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                next.setText("Delete");

                found2 = false;

                next.setOnClickListener(
                        view13 -> {
                            ValueEventListener valueEventListener = dbRefUsers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    // Try catch for handling exceptions
                                    try {
                                        // Searching the user by email
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            if (Objects.requireNonNull(postSnapshot.child("email").getValue()).toString().equals(email.getText().toString())) {
                                                dbRefUsers.child(Objects.requireNonNull(postSnapshot.getKey())).removeValue();
                                                dbRefActivities.child(postSnapshot.getKey()).removeValue();
                                                dbRefBookings.child(postSnapshot.getKey()).removeValue();
                                                dbRefOrders.child(postSnapshot.getKey()).removeValue();
                                                dbRefRoomReview.child(postSnapshot.getKey()).removeValue();
                                                resultText.setVisibility(View.VISIBLE);

                                                resultText.setText("User " + email.getText().toString() + " is not found");
                                                next.setVisibility(View.GONE);
                                                email.setVisibility(View.GONE);
                                                found2 = true;
                                            }
                                        }
                                        if (!found2) {
                                            resultText.setVisibility(View.VISIBLE);

                                            resultText.setText("User " + email.getText().toString() + " is not found");
                                            next.setVisibility(View.VISIBLE);
                                        }
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            });
                        });
            });
        }

        // Toast the warning message if something went wrong
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(), "Something went wrong ¯\\_(ツ)_/¯", Toast.LENGTH_SHORT).show();
        }

    }
}
