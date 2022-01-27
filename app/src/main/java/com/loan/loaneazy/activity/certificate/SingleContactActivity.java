package com.loan.loaneazy.activity.certificate;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.loan.loaneazy.R;
import com.loan.loaneazy.model.contacts.Contact;
import com.loan.loaneazy.utility.LoanConstants;
import com.loan.loaneazy.utility.MyHelper;
import com.loan.loaneazy.views.MyButton;
import com.loan.loaneazy.views.MyRadioButton;
import com.loan.loaneazy.views.MyTextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class SingleContactActivity extends AppCompatActivity {

    private MyButton btnConfirmSingle;
    private RecyclerView rvContactSingle;
    private ImageView ivBackSingle;
    private ProgressDialog dialog;
    private HashMap<String, Contact> myContacts;
    private String mContactNumber = null, mName = null;
    private ArrayList<Contact> mListContact;
    private MyContactAdapter mContsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_contact);
        btnConfirmSingle = (MyButton) findViewById(R.id.btnConfirmSingle);
        rvContactSingle = (RecyclerView) findViewById(R.id.rvContactSingle);
        ivBackSingle = (ImageView) findViewById(R.id.ivBackSingle);
        rvContactSingle.setLayoutManager(MyHelper.getLinearManager(this));
        ivBackSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!MyHelper.isPhonePermissionGranted(this)) {
                requestPermission();
            } else {
                fetchMyContacts();
            }
        } else {
            fetchMyContacts();
        }
        btnConfirmSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mContactNumber == null) {
                    Toast.makeText(SingleContactActivity.this, "please select contact", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("number", mContactNumber);
                    intent.putExtra("name", mName);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, LoanConstants.PHONE_REQUEST_CODE);
        } else {
            fetchMyContacts();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LoanConstants.PHONE_REQUEST_CODE) {
            if (MyHelper.isPhonePermissionGranted(this)) {
                fetchMyContacts();
            } else {
                boolean setting = false;
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRational = shouldShowRequestPermissionRationale(permission);
                        if (!showRational) {
                            //Log.e(TAG, "onRequestPermissionsResult: true : " + permission);
                            setting = true;
                            break;
                        }
                    }
                }
            }
        }
    }


    private void showProgress() {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
        }
        dialog.setMessage("please wait..");
        dialog.show();
    }

    private void hideProgress() {
        dialog.dismiss();
    }


    private void fetchMyContacts() {
        showProgress();
        myContacts = new HashMap<>();
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        if (cursor != null && !cursor.isClosed()) {
            cursor.getCount();
            while (cursor.moveToNext()) {
                int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (hasPhoneNumber == 1) {
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+", "");
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME_PRIMARY));
                    if (Patterns.PHONE.matcher(number).matches()) {
                        boolean hasPlus = String.valueOf(number.charAt(0)).equals("+");
                        number = number.replaceAll("[\\D]", "");
                        if (hasPlus) {
                            number = "+" + number;
                        }
                        Contact contact = new Contact(number, name);

                        String endTrim = MyHelper.getEndTrim(contact.getPhoneNumber());
                        // Log.e(TAG, "fetchMyContacts: Phone No :: " + number + "  Name :: " + name + "  EndTrim :: " + endTrim);
                        if (!myContacts.containsKey(endTrim))
                            myContacts.put(endTrim, contact);
                    }
                }
            }
            cursor.close();
        }


        refreshMyContactsCache(myContacts);

    }

    protected void refreshMyContactsCache(HashMap<String, Contact> contactsToSet) {

        mListContact = new ArrayList<>();
        for (Contact contact : contactsToSet.values()) {
            mListContact.add(contact);
        }
        Collections.sort(mListContact, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                String name1 = contact1.getName().toLowerCase();
                String name2 = contact2.getName().toLowerCase();
                return name1.compareTo(name2);
            }
        });

        mContsAdapter = new MyContactAdapter(this, mListContact);
        rvContactSingle.setAdapter(mContsAdapter);
        hideProgress();

    }

    public class MyContactAdapter extends RecyclerView.Adapter<MyContactAdapter.MyContactHolder> implements Filterable {
        boolean[] mCheckList;
        Context mContext;
        ArrayList<Contact> mListContact;
        ArrayList<Contact> mListContactFilter;
        public int lastSelectedPosition = -1;

        MyContactAdapter(Context mContext, ArrayList<Contact> mListContact) {
            this.mContext = mContext;
            this.mListContact = mListContact;
            this.mListContactFilter = mListContact;
            mCheckList = new boolean[mListContact.size()];
        }

        @NonNull
        @Override
        public MyContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_single_contact_item, parent, false);
            return new MyContactHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyContactHolder holder, int position) {
            holder.makeData();
        }

        @Override
        public int getItemCount() {
            return mListContactFilter.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        mListContactFilter = mListContact;
                    } else {
                        ArrayList<Contact> tempFilterList = new ArrayList<>();
                        for (Contact mContact : mListContact) {
                            if (mContact.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                tempFilterList.add(mContact);
                            }

                        }
                        mListContactFilter = tempFilterList;

                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = mListContactFilter;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    mListContactFilter = (ArrayList<Contact>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        class MyContactHolder extends RecyclerView.ViewHolder {
            MyTextView tvFirstLetter, tvFullName, tvMobileNumber;
            MyRadioButton rbtnSelect;

            MyContactHolder(@NonNull View itemView) {
                super(itemView);
                tvFirstLetter = itemView.findViewById(R.id.tvFirstLetter);
                tvFullName = itemView.findViewById(R.id.tvFullName);
                tvMobileNumber = itemView.findViewById(R.id.tvMobileNumber);
                rbtnSelect = itemView.findViewById(R.id.rbtnSelect);
                /*itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lastSelectedPosition = getAdapterPosition();
                        mContactNumber = mListContactFilter.get(lastSelectedPosition).getPhoneNumber();
                        mName = mListContactFilter.get(lastSelectedPosition).getName();
                        notifyDataSetChanged();
                    }
                });*/

                rbtnSelect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        lastSelectedPosition = getAdapterPosition();
                        mContactNumber = mListContactFilter.get(lastSelectedPosition).getPhoneNumber();
                        mName = mListContactFilter.get(lastSelectedPosition).getName();
                        notifyDataSetChanged();
                    }
                });
            }


            void makeData() {
                tvFirstLetter.setText(String.valueOf(mListContactFilter.get(getAdapterPosition()).getName().charAt(0)));
                tvFullName.setText(mListContactFilter.get(getAdapterPosition()).getName());
                tvMobileNumber.setText(mListContactFilter.get(getAdapterPosition()).getPhoneNumber());
                rbtnSelect.setChecked(lastSelectedPosition == getAdapterPosition());
            }
        }
    }
}