package lk.xrontech.watchparadise.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import lk.xrontech.watchparadise.linstener.OnItemClickListener;
import lk.xrontech.watchparadise.R;
import lk.xrontech.watchparadise.activity.AllProductsActivity;
import lk.xrontech.watchparadise.activity.SingleProductViewActivity;
import lk.xrontech.watchparadise.adapter.ProductAdapter;
import lk.xrontech.watchparadise.model.Product;

public class HomeFragment extends Fragment implements OnItemClickListener {
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Product> products;
    private RecyclerView productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();

        products = new ArrayList<>();

        productList = view.findViewById(R.id.productList);

        ImageSlider imageSlider = view.findViewById(R.id.imageSlider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.image_1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image_4, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        ProductAdapter productListAdapter = new ProductAdapter(products, getContext(), this::onItemClick);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);

        productList.setLayoutManager(gridLayoutManager);
        productList.setAdapter(productListAdapter);

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
                        productListAdapter.notifyDataSetChanged();
                    }
                });

        view.findViewById(R.id.seeAllProducts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AllProductsActivity.class));
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), SingleProductViewActivity.class);
        intent.putExtra("documentId", products.get(position).getDocumentId());
        startActivity(intent);
    }
}