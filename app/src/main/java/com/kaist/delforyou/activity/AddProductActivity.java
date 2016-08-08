package com.kaist.delforyou.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.kaist.delforyou.R;

/**
 * Created by user on 2016-08-05.
 */
public class AddProductActivity extends Activity {

    private static final String TAG = AddProductActivity.class.getSimpleName();
    public Spinner itemCategorySpinner;
    public Spinner itemDimensionSpinner;
    public EditText itemDescription;
    public EditText itemCount;
    public DeliveryItem item = new DeliveryItem();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addproduct);

        String[] itemDescription = {"핸드폰","TV","냉장고","세탁기","테블릿PC","모니터","도서"};
        ArrayAdapter<String> itemDescriptionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemDescription);
        itemCategorySpinner= (Spinner)findViewById(R.id.spinner1);
        itemCategorySpinner.setAdapter(itemDescriptionAdapter);
        itemDescriptionAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//        spinner.setSelection(2);

        String[] itemDimension = {"극소(80cm, 2kg 이하)","소(100cm, 5kg 이하)","중(120cm, 15kg 이하)","대(160cm, 25kg 이하)","특대(160cm, 25kg 초과)"};
        ArrayAdapter<String> itemDimensionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, itemDimension);
        itemDimensionSpinner= (Spinner)findViewById(R.id.spinner2);
        itemDimensionSpinner.setAdapter(itemDimensionAdapter);
        itemDimensionAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);


        //itemCategory처리
        itemCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent
                    , View v,
                                       int position, long id) {
                int index=  itemCategorySpinner.getSelectedItemPosition();
                if(index==0){
                    item.categoryId = "000";
                    item.categoryName ="핸드폰";

                }else if(index==1){
                    item.categoryId = "001";
                    item.categoryName ="TV";
                }else if(index==2){
                    item.categoryId = "002";
                    item.categoryName ="냉장고";
                } else if(index==3){
                    item.categoryId = "003";
                    item.categoryName ="세탁기";
                } else if(index==4){
                    item.categoryId = "004";
                    item.categoryName ="테블릿PC";
                } else if(index==5){
                    item.categoryId = "005";
                    item.categoryName ="모니터";
                } else if(index==6){
                    item.categoryId = "006";
                    item.categoryName ="도서";
                }
                Log.d(TAG,"Category ID: "+ item.categoryId);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //itemDimension처리
        itemDimensionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v,
                                       int position, long id) {
                int index=  itemDimensionSpinner.getSelectedItemPosition();
                if(index==0){
                    item.dimension="000";
                    item.dimensionName = "극소(160cm, 25kg 이하)";
                }else if(index==1){
                    item.dimension="001";
                    item.dimensionName = "소(100cm, 5kg 이하)";
                }else if(index==2){
                    item.dimension="002";
                    item.dimensionName = "중(120cm, 15kg 이하)";

                } else if(index==3){
                    item.dimension="003";
                    item.dimensionName = "대(160cm, 25kg 이하)";

                } else if(index==4){
                    item.dimension="004";
                    item.dimensionName = "특대(160cm, 25kg 초과)";

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    //확인 버튼 눌렀을 시,
    public void addProductConfirm(View v) {
        Intent intent = getIntent();
//        RequestDelivery RDObject= (RequestDelivery) intent.getParcelableExtra("RDObject");
//        DeliveryItem item= RDObject.itemList.get(0);

                //itemDescription처리
        itemDescription = (EditText) findViewById(R.id.itemDescription);
        item.description =itemDescription.getText().toString();

        //itemCount처리
        itemCount=(EditText) findViewById(R.id.itemCount);
        item.count = Integer.parseInt(itemCount.getText().toString());

//       item.categoryId = "000";
//        item.count = 3;
//        item.description = "LG G5";
//        item.dimension = "000";

//        RDObject.itemList.add(item);
        intent.putExtra("deliveryItem",item);
        setResult(RESULT_OK, intent);
        finish();
//        RequestDelivery.DeliveryItem test2 = RDObject.new DeliveryItem();
//        test2.categoryId = "005";
//        test2.count = 1;
//        test2.description = "LG TV";
//        test2.dimension = "004";
    }

            //취소 버튼 눌렀을 시,
    public void addProductCancle(View v) {
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
