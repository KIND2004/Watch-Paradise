package lk.xrontech.watchparadise.adapter;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.model.Cart;
import lk.xrontech.watchparadise.model.Product;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<Cart> carts;
    private Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    public CartAdapter(ArrayList<Cart> carts, Context context) {
        this.carts = carts;
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cart_single_row, parent, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        Cart cart = carts.get(position);
        holder.cartProductQty.setText(String.valueOf(cart.getQuantity()));
        firebaseFirestore.collection("products")
                .document(cart.getProductId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            Product product = task.getResult().toObject(Product.class);
                            holder.cartProductTitle.setText(product.getTitle());
                            holder.cartProductPrice.setText("LKR. " + product.getPrice());
                            holder.cartProductTotalPrice.setText("LKR. " + (cart.getQuantity() * product.getPrice()));

                            firebaseStorage.getReference("product-images/" + product.getImagePath())
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Picasso.get()
                                                    .load(uri)
                                                    .fit()
                                                    .into(holder.cartProductImage);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context.getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });

                            holder.btnCartQtyAddition.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int qty = Integer.parseInt(holder.cartProductQty.getText().toString());
                                    if (qty < 5) {
                                        int newQty = qty + 1;
                                        cart.setQuantity(newQty);
                                        firebaseFirestore.collection("cart")
                                                .document(cart.getCartId())
                                                .set(cart)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        holder.cartProductQty.setText(String.valueOf(newQty));
                                                        holder.cartProductTotalPrice.setText("LKR. " + (product.getPrice() * newQty));
                                                    }
                                                });
                                    }
                                }
                            });

                            holder.btnCartQtySubtraction.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int qty = Integer.parseInt(holder.cartProductQty.getText().toString());
                                    if (qty > 1) {
                                        int newQty = qty - 1;
                                        cart.setQuantity(newQty);
                                        firebaseFirestore.collection("cart")
                                                .document(cart.getCartId())
                                                .set(cart)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        holder.cartProductQty.setText(String.valueOf(newQty));
                                                        holder.cartProductTotalPrice.setText("LKR. " + (product.getPrice() * newQty));
                                                    }
                                                });
                                    }
                                }
                            });

                            holder.cartRemoveProduct.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    firebaseFirestore.collection("cart")
                                            .document(cart.getCartId())
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    carts.remove(position);
                                                    notifyDataSetChanged();
                                                }
                                            });
                                }
                            });

                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cartProductTitle, cartProductPrice, cartProductTotalPrice, cartProductQty, btnCartQtyAddition, btnCartQtySubtraction;
        ImageView cartProductImage;
        ImageButton cartRemoveProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cartProductTitle = itemView.findViewById(R.id.cartProductTitle);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            cartProductTotalPrice = itemView.findViewById(R.id.cartProductTotalPrice);
            cartProductQty = itemView.findViewById(R.id.cartProductQty);
            cartProductImage = itemView.findViewById(R.id.cartProductImage);

            btnCartQtyAddition = itemView.findViewById(R.id.btnCartQtyAddition);
            btnCartQtySubtraction = itemView.findViewById(R.id.btnCartQtySubtraction);

            cartRemoveProduct = itemView.findViewById(R.id.cartRemoveProduct);
        }
    }
}
