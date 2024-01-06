package lk.xrontech.watchparadise.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
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
import java.util.List;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.adapter.CartAdapter;
import lk.xrontech.watchparadise.model.Cart;
import lk.xrontech.watchparadise.model.Product;

public class CartActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Cart> carts;
    private RecyclerView cartProductList;
    private TextView cartSubTotal, cartTotal;
    private double total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        firebaseFirestore = FirebaseFirestore.getInstance();

        carts = new ArrayList<>();

        cartProductList = findViewById(R.id.cartProductList);
        cartSubTotal = findViewById(R.id.cartSubTotal);
        cartTotal = findViewById(R.id.cartTotal);

        CartAdapter cartAdapter = new CartAdapter(carts, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());

        cartProductList.setLayoutManager(linearLayoutManager);
        cartProductList.setAdapter(cartAdapter);

        firebaseFirestore.collection("cart")
                .whereEqualTo("userId", getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        carts.clear();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            String documentId = snapshot.getId();
                            Cart cart = snapshot.toObject(Cart.class);
                            cart.setCartId(documentId);
                            carts.add(cart);
                        }
                        cartAdapter.notifyDataSetChanged();
                    }
                });

        firebaseFirestore.collection("cart")
                .whereEqualTo("userId", getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null))
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (value.isEmpty()) {
                            cartSubTotal.setText("00.0");
                            cartTotal.setText("00.0");
                        } else {
                            for (QueryDocumentSnapshot snapshot : value) {
                                Cart cart = snapshot.toObject(Cart.class);
                                total = 00.0;
                                firebaseFirestore.collection("products")
                                        .document(cart.getProductId())
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                Product product = value.toObject(Product.class);
                                                if (product != null) {
                                                    Double productPrice = product.getPrice();
                                                    total = total + (productPrice * cart.getQuantity());
                                                    cartSubTotal.setText("LKR. " + total);
                                                    cartTotal.setText("LKR. " + total);
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });

        findViewById(R.id.cartViewBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }
}