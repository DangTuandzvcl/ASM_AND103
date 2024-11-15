
package com.tuanda.ph49753.asm_and103;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    ListView lvMain;
    List<CarModel> listCarModel;

    CarAdapter carAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        lvMain = findViewById(R.id.listviewMain);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);

        Call<List<CarModel>> call = apiService.getCars();

        call.enqueue(new Callback<List<CarModel>>() {
            @Override
            public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                if (response.isSuccessful()) {
                    listCarModel = response.body();

                    carAdapter = new CarAdapter(getApplicationContext(), listCarModel);

                    lvMain.setAdapter(carAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<CarModel>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });

        findViewById(R.id.btn_add).setOnClickListener(v -> {
            // Tạo một AlertDialog để nhập thông tin xe mới
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            // Inflate layout từ XML cho AlertDialog
            builder.setTitle("Nhập thông tin xe");
            builder.setView(inflater.inflate(R.layout.dialog_add_car, null));

            builder.setPositiveButton("OK", (dialog, which) -> {
                // Lấy thông tin từ các EditText trong dialog
                AlertDialog alertDialog = (AlertDialog) dialog;
                EditText edtName = alertDialog.findViewById(R.id.edt_name);
                EditText edtYear = alertDialog.findViewById(R.id.edt_year);
                EditText edtBrand = alertDialog.findViewById(R.id.edt_brand);
                EditText edtPrice = alertDialog.findViewById(R.id.edt_price);

                if (edtName != null && edtYear != null && edtBrand != null && edtPrice != null) {
                    String name = edtName.getText().toString();
                    String year = edtYear.getText().toString();
                    String brand = edtBrand.getText().toString();
                    int price = Integer.parseInt(edtPrice.getText().toString());

                    // Tạo đối tượng CarModel từ dữ liệu đã nhập
                    CarModel newCar = new CarModel("", name, year, brand, price);

                    // Gọi API để thêm xe mới
                    Call<List<CarModel>> callAddXe = apiService.addCar(newCar);

                    callAddXe.enqueue(new Callback<List<CarModel>>() {
                        @Override
                        public void onResponse(Call<List<CarModel>> call, Response<List<CarModel>> response) {
                            if (response.isSuccessful()) {
                                // Cập nhật danh sách và hiển thị lại
                                listCarModel.clear();
                                listCarModel.addAll(response.body());
                                carAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<CarModel>> call, Throwable t) {
                            Log.e("Main", t.getMessage());
                        }
                    });
                }
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            builder.create().show();
        });


    }
}
