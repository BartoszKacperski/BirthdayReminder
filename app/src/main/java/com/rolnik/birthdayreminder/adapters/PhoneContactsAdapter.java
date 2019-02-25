package com.rolnik.birthdayreminder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rolnik.birthdayreminder.R;
import com.rolnik.birthdayreminder.databinding.PhonecontactDropdownSpinnerBinding;
import com.rolnik.birthdayreminder.databinding.PhonecontactSpinnerLayoutBinding;
import com.rolnik.birthdayreminder.model.PhoneContact;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

public class PhoneContactsAdapter extends ArrayAdapter<PhoneContact> {

    private List<PhoneContact> phoneContacts;

    public PhoneContactsAdapter(@NonNull Context context, int resource, List<PhoneContact> phoneContacts) {
        super(context, resource, 0, phoneContacts);
        this.phoneContacts = phoneContacts;
    }


    public void add(PhoneContact phoneContact){
        phoneContacts.add(phoneContact);
        notifyDataSetChanged();
    }

    public void addAll(List<PhoneContact> phoneContactList){
        for(PhoneContact phoneContact : phoneContactList){
            this.add(phoneContact);
        }
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        PhoneContactDropdownViewHolder holder;

        if(convertView == null) {
            PhonecontactDropdownSpinnerBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.phonecontact_dropdown_spinner, parent, false);
            holder = new PhoneContactDropdownViewHolder(binding);
            holder.view.setTag(holder);
        } else {
            holder = (PhoneContactDropdownViewHolder) convertView.getTag();
        }


        PhoneContact phoneContact = phoneContacts.get(position);

        holder.bind(phoneContact);

        return holder.view;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        PhoneContactViewHolder holder;

        if(convertView == null) {
            PhonecontactSpinnerLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.phonecontact_spinner_layout, parent, false);
            holder = new PhoneContactViewHolder(binding);
            holder.view.setTag(holder);
        } else {
            holder = (PhoneContactViewHolder) convertView.getTag();
        }


        PhoneContact phoneContact = phoneContacts.get(position);

        holder.bind(phoneContact);

        return holder.view;
    }

    @Override
    public int getPosition(PhoneContact phoneContact){
        return phoneContacts.indexOf(phoneContact);
    }

    private static class PhoneContactViewHolder{
        private View view;
        private PhonecontactSpinnerLayoutBinding binding;

        public PhoneContactViewHolder(PhonecontactSpinnerLayoutBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }

        public void bind(PhoneContact phoneContact){
            binding.setPhoneContact(phoneContact);
        }
    }

    private static class PhoneContactDropdownViewHolder{
        private View view;
        private PhonecontactDropdownSpinnerBinding binding;

        public PhoneContactDropdownViewHolder(PhonecontactDropdownSpinnerBinding binding) {
            this.view = binding.getRoot();
            this.binding = binding;
        }

        public void bind(PhoneContact phoneContact){
            binding.setPhoneContact(phoneContact);
        }
    }
}
