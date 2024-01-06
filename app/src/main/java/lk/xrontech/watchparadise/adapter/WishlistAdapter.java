package lk.xrontech.watchparadise.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

import lk.xrontech.watchparadise.activity.CartActivity;
import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.model.Cart;
import lk.xrontech.watchparadise.model.Product;
import lk.xrontech.watchparadise.model.Wishlist;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private ArrayList<Wishlist> wishlists;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    public WishlistAdapter(ArrayList<Wishlist> wishlists, Context context) {
        this.wishlists = wishlists;
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public WishlistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_wishlist_single_row, parent, false);
        return new WishlistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistAdapter.ViewHolder holder, int position) {
        Wishlist wishlist = wishlists.get(position);
        firebaseFirestore.collection("products")
                .document(wishlist.getProductId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Product product = task.getResult().toObject(Product.class);
                            if (product != null) {
                                holder.wishlistProductTitle.setText(product.getTitle());
                                holder.wishlistProductPrice.setText("LKR. " + product.getPrice());
                                int qty = Integer.parseInt(holder.wishlistProductQty.getText().toString());
                                holder.wishlistProductTotalPrice.setText("LKR. " + (qty * product.getPrice()));

                                firebaseStorage.getReference("product-images/" + product.getImagePath())
                                        .getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Picasso.get()
                                                        .load(uri)
                                                        .fit()
                                                        .into(holder.wishlistProductImage);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                                holder.btnWishlistQtyAddition.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int qty = Integer.parseInt(holder.wishlistProductQty.getText().toString());
                                        if (qty < 5) {
                                            int newQty = qty + 1;
                                            holder.wishlistProductQty.setText(String.valueOf(newQty));
                                            holder.wishlistProductTotalPrice.setText("LKR. " + (product.getPrice() * newQty));
                                        }
                                    }
                                });

                                holder.btnWishlistQtySubtraction.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        int qty = Integer.parseInt(holder.wishlistProductQty.getText().toString());
                                        if (qty > 1) {
                                            int newQty = qty - 1;
                                            holder.wishlistProductQty.setText(String.valueOf(newQty));
                                            holder.wishlistProductTotalPrice.setText("LKR. " + (product.getPrice() * newQty));
                                        }
                                    }
                                });

                                holder.btnWishlistRemoveProduct.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        firebaseFirestore.collection("wishlist")
                                                .document(wishlist.getWishlistId())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        wishlists.remove(position);
                                                        notifyDataSetChanged();
                                                    }
                                                });
                                    }
                                });

                                holder.btnWishlistAddToCart.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        firebaseFirestore.collection("cart")
                                                .whereEqualTo("productId", wishlist.getProductId())
                                                .whereEqualTo("userId", wishlist.getUserId())
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            QuerySnapshot result = task.getResult();
                                                            if (result.isEmpty()) {
                                                                int cartProductQty = Integer.parseInt(holder.wishlistProductQty.getText().toString());
                                                                Cart cart = new Cart();
                                                                cart.setProductId(wishlist.getProductId());
                                                                cart.setUserId(wishlist.getUserId());
                                                                cart.setQuantity(cartProductQty);
                                                                firebaseFirestore.collection("cart")
                                                                        .add(cart)
                                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                            @Override
                                                                            public void onSuccess(DocumentReference documentReference) {
                                                                                Intent cartIntent = new Intent(context, CartActivity.class);
                                                                                context.startActivity(cartIntent);
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(context, "Failed to Add Product to Cart", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        });
                                                            } else {
                                                                Toast.makeText(context, "This Product Already Exist in Cart", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }

                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return wishlists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView wishlistProductTitle, wishlistProductPrice, wishlistProductTotalPrice, wishlistProductQty, btnWishlistQtyAddition, btnWishlistQtySubtraction;
        ImageView wishlistProductImage;
        ImageButton btnWishlistRemoveProduct, btnWishlistAddToCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            wishlistProductTitle = itemView.findViewById(R.id.wishlistProductTitle);
            wishlistProductPrice = itemView.findViewById(R.id.wishlistProductPrice);
            wishlistProductTotalPrice = itemView.findViewById(R.id.wishlistProductTotalPrice);
            wishlistProductQty = itemView.findViewById(R.id.wishlistProductQty);
            wishlistProductImage = itemView.findViewById(R.id.wishlistProductImage);

            btnWishlistQtyAddition = itemView.findViewById(R.id.btnWishlistQtyAddition);
            btnWishlistQtySubtraction = itemView.findViewById(R.id.btnWishlistQtySubtraction);

            btnWishlistRemoveProduct = itemView.findViewById(R.id.btnWishlistRemoveProduct);
            btnWishlistAddToCart = itemView.findViewById(R.id.btnWishlistAddToCart);
        }
    }
}
