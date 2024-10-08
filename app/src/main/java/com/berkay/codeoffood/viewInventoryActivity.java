package com.berkay.codeoffood;

import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class viewInventoryActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    RecyclerView mrecyclerview;
    DatabaseReference mdatabaseReference;
    private FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder> firebaseRecyclerAdapter;
   private TextView totalnoofitem, totalnoofsum;
   private int counttotalnoofitem =0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_inventory);
        totalnoofitem= findViewById(R.id.totalnoitem);
        totalnoofsum = findViewById(R.id.totalsum);
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser users = firebaseAuth.getCurrentUser();
        String finaluser=users.getEmail();
        String resultemail = finaluser.replace(".","");
        mdatabaseReference = FirebaseDatabase.getInstance().getReference("Users").child(resultemail).child("Items");
        mrecyclerview = findViewById(R.id.recyclerViews);
        mrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        mrecyclerview.setHasFixedSize(true);


        mdatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    counttotalnoofitem = (int) dataSnapshot.getChildrenCount();
                    totalnoofitem.setText(Integer.toString(counttotalnoofitem));
                }else{
                    totalnoofitem.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    mdatabaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            int sum=0;
            for(DataSnapshot ds : dataSnapshot.getChildren()){
                Map<String,Object> map = (Map<String, Object>) ds.getValue();
                Object price = map.get("itemprice");
                int pValue = Integer.parseInt(String.valueOf(price));
                sum += pValue;
                totalnoofsum.setText(String.valueOf(sum));

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });

    }

    @Override
    protected  void  onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Items, scanItemsActivity.UsersViewHolder>(
                new FirebaseRecyclerOptions.Builder<Items>().setQuery(mdatabaseReference, Items.class).build()
        ) {
            @Override
            protected void onBindViewHolder(@NonNull scanItemsActivity.UsersViewHolder holder, int position, @NonNull Items model) {
                holder.setDetails(getApplicationContext(), model.getItembarcode(), model.getItemcategory(), model.getItemname(), model.getItemprice());
            }

            @NonNull
            @Override
            public scanItemsActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout, parent, false);
                return new scanItemsActivity.UsersViewHolder(view);
            }
        };

        mrecyclerview.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseRecyclerAdapter != null) {
            firebaseRecyclerAdapter.stopListening();
        }
    }

}
