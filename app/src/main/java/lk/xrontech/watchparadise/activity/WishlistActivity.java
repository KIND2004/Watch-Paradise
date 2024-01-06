package lk.xrontech.watchparadise.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.adapter.CartAdapter;
import lk.xrontech.watchparadise.adapter.WishlistAdapter;
import lk.xrontech.watchparadise.model.Cart;
import lk.xrontech.watchparadise.model.Product;
import lk.xrontech.watchparadise.model.Wishlist;

public class WishlistActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Wishlist> wishlists;
    private RecyclerView wishlistProductList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        firebaseFirestore = FirebaseFirestore.getInstance();

        wishlists = new ArrayList<>();

        wishlistProductList = findViewById(R.id.wishlistProductList);

        WishlistAdapter wishlistAdapter = new WishlistAdapter(wishlists, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        wishlistProductList.setLayoutManager(linearLayoutManager);
        wishlistProductList.setAdapter(wishlistAdapter);

        firebaseFirestore.collection("wishlist")
                .whereEqualTo("userId", getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        wishlists.clear();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            String documentId = snapshot.getId();
                            Wishlist wishlist = snapshot.toObject(Wishlist.class);
                            wishlist.setWishlistId(documentId);
                            wishlists.add(wishlist);
                        }
                        wishlistAdapter.notifyDataSetChanged();
                    }
                });

    }
}