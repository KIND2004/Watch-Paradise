package lk.xrontech.watchparadise.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.xrontech.watchparadise.linstener.OnItemClickListener;
import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.adapter.AllProductsAdapter;
import lk.xrontech.watchparadise.model.Product;

public class AllProductsActivity extends AppCompatActivity implements OnItemClickListener {

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Product> products;
    private RecyclerView allProductsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_products);

        firebaseFirestore = FirebaseFirestore.getInstance();

        products = new ArrayList<>();

        allProductsList = findViewById(R.id.allProductsList);

        AllProductsAdapter allProductsAdapter = new AllProductsAdapter(products, this, this::onItemClick);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        allProductsList.setLayoutManager(gridLayoutManager);
        allProductsList.setAdapter(allProductsAdapter);

        firebaseFirestore.collection("products")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        products.clear();
                        for (QueryDocumentSnapshot snapshot : value) {
                            String documentId = snapshot.getId();
                            Product product = snapshot.toObject(Product.class);
                            product.setDocumentId(documentId);
                            products.add(product);
                        }
                        allProductsAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getApplicationContext(), SingleProductViewActivity.class);
        intent.putExtra("documentId", products.get(position).getDocumentId());
        startActivity(intent);
    }
}