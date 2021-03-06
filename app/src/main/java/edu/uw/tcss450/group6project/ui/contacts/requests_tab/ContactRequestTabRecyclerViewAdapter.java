package edu.uw.tcss450.group6project.ui.contacts.requests_tab;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import edu.uw.tcss450.group6project.R;
import edu.uw.tcss450.group6project.databinding.FragmentContactRequestCardBinding;
import edu.uw.tcss450.group6project.model.UserInfoViewModel;
import edu.uw.tcss450.group6project.ui.contacts.list_tab.ContactListTabRecyclerViewAdapter;

/** The RecyclerView that handles the scrolling list of contact requests
 * @author chasealder
 */
public class ContactRequestTabRecyclerViewAdapter extends
        RecyclerView.Adapter<ContactRequestTabRecyclerViewAdapter.ContactRequestViewHolder> {

    /**
     * The list of contacts.
     */
    private final List<ContactRequest> mContactRequests;
    private Fragment mFragment;
    private UserInfoViewModel mUserInfoViewModel;

    /**
     * Constructs the RecyclerView.
     *
     * @param contactRequests a list of contacts
     */
    public ContactRequestTabRecyclerViewAdapter(List<ContactRequest> contactRequests, Fragment fragment, UserInfoViewModel userInfoViewModel) {
        this.mContactRequests = contactRequests;
        this.mFragment = fragment;
    }

    @NonNull
    @Override
    public ContactRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactRequestViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_request_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactRequestViewHolder holder, int position) {
        holder.setContactRequest(mContactRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return mContactRequests.size();
    }

    /**
     * Represents an individual row View from the list of rows in the Contact Recycler View.
     *
     * @author Robert M
     * @version 3 November 2020
     */
    public class ContactRequestViewHolder extends RecyclerView.ViewHolder {

        /**
         * The View.
         */
        public final View mView;

        /**
         * The binding for the contact list card.
         */
        public FragmentContactRequestCardBinding binding;

        /**
         * The contact.
         */
        private ContactRequest mContactRequest;

        private ContactRequestTabViewModel mContactRequestTabViewModel;

        /**
         * Constructs the contact view.
         *
         * @param view the view
         */
        public ContactRequestViewHolder(View view) {
            super(view);
            mView = view;

            // not sure if these are needed
            ViewModelProvider provider = new ViewModelProvider(mFragment.getActivity());
            mContactRequestTabViewModel = provider.get(ContactRequestTabViewModel.class);
            mUserInfoViewModel = provider.get(UserInfoViewModel.class);

            binding = FragmentContactRequestCardBinding.bind(view);
            binding.buttonContactRequestConfirm.setOnClickListener(this::handleConfirm);
            binding.buttonContactRequestDeny.setOnClickListener(this::handleDeny);
        }

        /** Used to deny a contact request. Called by hitting the deny button on a contact request
         *
         * @param button the deny button
         */
        private void handleDeny(final View button) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mContactRequestTabViewModel.connectDeny(mUserInfoViewModel.getJWT(),mContactRequest.getMemberId());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(button.getContext());
            builder.setMessage("Deny contact request from " + mContactRequest.getUsername() + "?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        /** Used to confirm a contact request. Called by hitting the confirm button on a contact request
         *
         * @param button the confirm button
         */
        private void handleConfirm(final View button) {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        mContactRequestTabViewModel.connectConfirm(mUserInfoViewModel.getJWT(),mContactRequest.getMemberId());
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(button.getContext());
            builder.setMessage("Confirm contact request from " + mContactRequest.getUsername() + "?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        /**
         * Sets each card view for the contact request list.
         *
         * @param contactRequest the contact request being set
         */
        void setContactRequest(final ContactRequest contactRequest) {
            mContactRequest = contactRequest;
            binding.textContactRequestName.setText(contactRequest.getUsername());
        }
    }
}
