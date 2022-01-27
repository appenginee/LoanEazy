package com.loan.loaneazy.utility;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.Patterns;

import com.google.gson.Gson;
import com.loan.loaneazy.model.epoc.Contact;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class ContactHelper {

    public static ArrayList<Contact> fetchMyContacts(Context mCtx) {
        //showProgress();
        HashMap<String, Contact> myContacts =new HashMap<>();
        Cursor cursor = mCtx.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
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

                        String endTrim = getEndTrim(contact.getPhoneNumber());
                        // Log.e(TAG, "fetchMyContacts: Phone No :: " + number + "  Name :: " + name + "  EndTrim :: " + endTrim);
                        if (!myContacts.containsKey(endTrim))
                            myContacts.put(endTrim, contact);
                    }
                }
            }
            cursor.close();
        }


        return refreshMyContactsCache(myContacts);

    }

    public static String getEndTrim(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() >= 8 ? phoneNumber.substring(phoneNumber.length() - 7) : phoneNumber;
    }

    public static ArrayList<Contact> refreshMyContactsCache(HashMap<String, Contact> contactsToSet) {

        ArrayList<Contact> mListContact = new ArrayList<>(contactsToSet.values());
        Collections.sort(mListContact, new Comparator<Contact>() {
            @Override
            public int compare(Contact contact1, Contact contact2) {
                String name1 = contact1.getName().toLowerCase();
                String name2 = contact2.getName().toLowerCase();
                return name1.compareTo(name2);
            }
        });


        return mListContact;
    }
}
