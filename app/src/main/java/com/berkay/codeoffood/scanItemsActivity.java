package com.berkay.codeoffood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class scanItemsActivity extends AppCompatActivity {
    public static EditText resultsearcheview;
    private FirebaseAuth firebaseAuth;
    private ImageButton scantosearch;
    private Button searchbtn;
    private RecyclerView mrecyclerview;
    private DatabaseReference mdatabaseReference;
    private FirebaseRecyclerAdapter<Items, UsersViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_items);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser = users.getEmail();
        String resultemail = finaluser.replace(".", "");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("Items");
        resultsearcheview = findViewById(R.id.searchfield);
        scantosearch = findViewById(R.id.imageButtonsearch);
        searchbtn = findViewById(R.id.searchbtnn);

        mrecyclerview = findViewById(R.id.recyclerViews);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerview.setHasFixedSize(true);

        scantosearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ScanCodeActivitysearch.class));
            }
        });

        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchtext = resultsearcheview.getText().toString();
                firebasesearch(searchtext);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

    public void firebasesearch(String searchtext) {
        Query firebaseSearchQuery = mdatabaseReference.orderByChild("itembarcode").startAt(searchtext).endAt(searchtext + "\uf8ff");
        FirebaseRecyclerOptions<Items> options =
                new FirebaseRecyclerOptions.Builder<Items>()
                        .setQuery(firebaseSearchQuery, Items.class)
                        .build();
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_layout, parent, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder viewHolder, int position, @NonNull Items model) {
                viewHolder.setDetails(getApplicationContext(), model.getItembarcode(), model.getItemcategory(), model.getItemname(), model.getItemprice());
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                // Handle data change event
            }
        };
        mrecyclerview.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setDetails(Context ctx, String itembarcode, String itemcategory, String itemname, String itemprice) {
            TextView item_barcode = mView.findViewById(R.id.viewitembarcode);
            TextView item_name = mView.findViewById(R.id.viewitemname);
            TextView item_category = mView.findViewById(R.id.viewitemcategory);
            TextView item_price = mView.findViewById(R.id.viewitemprice);

            item_barcode.setText(itembarcode);
            item_category.setText(itemcategory);
            item_name.setText(itemname);
            item_price.setText(itemprice);
        }
    }
}
