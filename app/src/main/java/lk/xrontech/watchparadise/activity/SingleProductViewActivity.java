package lk.xrontech.watchparadise.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.model.Cart;
import lk.xrontech.watchparadise.model.Product;
import lk.xrontech.watchparadise.model.Wishlist;

public class SingleProductViewActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private TextView singleViewProductTitle, singleViewProductPrice, singleViewProductDescription, txtQuantity;
    private ImageView singleViewProductImage;
    private Product product;
    private SharedPreferences userDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        userDetails = getSharedPreferences("user_details", MODE_PRIVATE);
        String userEmail = userDetails.getString("email", null);

        singleViewProductTitle = findViewById(R.id.singleViewProductTitle);
        singleViewProductPrice = findViewById(R.id.singleViewProductPrice);
        singleViewProductDescription = findViewById(R.id.singleViewProductDescription);
        singleViewProductImage = findViewById(R.id.singleViewProductImage);

        txtQuantity = findViewById(R.id.quantity);
        txtQuantity.setText("1");

        Intent intent = getIntent();

        if (intent != null && intent.getExtras() != null) {
            Bundle extras = intent.getExtras();

            String documentId = extras.getString("documentId");

            firebaseFirestore.collection("products")
                    .document(documentId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                product = task.getResult().toObject(Product.class);
                                singleViewProductTitle.setText(product.getTitle());
                                singleViewProductPrice.setText("LKR. " + product.getPrice());
                                singleViewProductDescription.setText(product.getDescription());

                                firebaseStorage.getReference("product-images/" + product.getImagePath())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .into(singleViewProductImage);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                            }
                        }
                    });

            findViewById(R.id.btnCart).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userEmail == null) {
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                    } else {
                        firebaseFirestore.collection("cart")
                                .whereEqualTo("productId", documentId)
                                .whereEqualTo("userId", getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot result = task.getResult();
                                            if (result.isEmpty()) {
                                                Cart cart = new Cart();
                                                cart.setProductId(documentId);
                                                cart.setUserId(getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null));
                                                cart.setQuantity(Integer.parseInt(txtQuantity.getText().toString()));
                                                firebaseFirestore.collection("cart")
                                                        .add(cart)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Intent cartIntent = new Intent(getApplicationContext(), CartActivity.class);
                                                                startActivity(cartIntent);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Failed to Add Product to Cart", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getApplicationContext(), "This Product Already Exist in Cart", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }
                }
            });

            findViewById(R.id.btnWishlist).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userEmail == null) {
                        startActivity(new Intent(getApplicationContext(), AuthActivity.class));
                    } else {
                        firebaseFirestore.collection("wishlist")
                                .whereEqualTo("productId", documentId)
                                .whereEqualTo("userId", getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null))
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            QuerySnapshot result = task.getResult();
                                            if (result.isEmpty()) {
                                                Wishlist wishlist = new Wishlist();
                                                wishlist.setProductId(documentId);
                                                wishlist.setUserId(getSharedPreferences("user_details", MODE_PRIVATE).getString("documentId", null));
                                                firebaseFirestore.collection("wishlist")
                                                        .add(wishlist)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Intent cartIntent = new Intent(getApplicationContext(), WishlistActivity.class);
                                                                startActivity(cartIntent);
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Failed to Add Product to Wishlist", Toast.LENGTH_LONG).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getApplicationContext(), "This Product Already Exist in Wishlist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                });
                    }
                }
            });

        }

        findViewById(R.id.btnQtyAddition).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(txtQuantity.getText().toString());
                if (qty < 5) {
                    int newQty = qty + 1;
                    txtQuantity.setText(String.valueOf(newQty));
                    updatePrice(newQty);
                }
            }
        });

        findViewById(R.id.btnQtySubtraction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(txtQuantity.getText().toString());
                if (qty > 1) {
                    int newQty = qty - 1;
                    txtQuantity.setText(String.valueOf(newQty));
                    updatePrice(newQty);
                }
            }
        });

        findViewById(R.id.singleProductBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    void updatePrice(int qty) {
        Double price = product.getPrice();
        double updatePrice = price * qty;
        singleViewProductPrice.setText("LKR. " + updatePrice);
    }

}