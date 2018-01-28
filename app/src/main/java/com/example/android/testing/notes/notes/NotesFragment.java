/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.notes.notes;

import com.example.android.testing.notes.Injection;
import com.example.android.testing.notes.addnote.AddNoteActivity;
import com.example.android.testing.notes.datastructures.linkedlist;
import com.example.android.testing.notes.notedetail.NoteDetailActivity;
import com.example.android.testing.notes.R;
import com.example.android.testing.notes.data.Note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Display a grid of {@link Note}s
 */
public class NotesFragment extends Fragment implements NotesContract.View {

    private static final int REQUEST_ADD_NOTE = 1;

    private NotesContract.UserActionsListener mActionsListener;

    private NotesAdapter mListAdapter;

    public NotesFragment() {
        // Requires empty public constructor
    }

    public static NotesFragment newInstance() {
        return new NotesFragment();
    }

    //______________________________________________________________________________________________

    ArrayList<String> permutation(String str) {
        ArrayList<String> permutations = new ArrayList<>();
        permutation(str, "", permutations);
        return permutations;
    }

    void permutation(String str, String prefix, ArrayList<String> permutations){
        if(str.length() == 0) {
            permutations.add(prefix);
        } else {
            for (int i = 0; i < str.length(); i++) {
                String rem = str.substring(0,i) + str.substring(i+1);
                permutation(rem, prefix + str.charAt(i), permutations);
            }
        }
    }

    boolean isUnique(String str) {
        HashSet<String> previous = new HashSet<>();
        for(int i = 0; i<str.length(); i++){
            String ch = str.substring(i,i+1);
          if (!previous.contains(ch)){
              previous.add(ch);
            } else {
              return false;
            }
        }
        return true;
    }

    boolean isUnique2(String str) {
        char[] chars = str.toCharArray();
        String[] table = new String[256];
        for (char ch: chars){
            int ascii = (int) ch;
            int index = ascii % 256;
            if(table[index]==null){
                table[index] = new String();
            }
            if(!table[index].contains(String.valueOf(ch))){
                table[index] = table[index].concat(String.valueOf(ch));
            } else {
                return false;
            }
        }
        return true;
    }

    public boolean isUniqueChars(String str) {
        if (str.length() > 26) { // Only 26 characters
            return false;
        }
        int checker = 0;
        for (int i = 0; i < str.length(); i++) {
            int val = str.charAt(i) - 'a';
            if ((checker & (1 << val)) > 0) return false;
            checker |= (1 << val);
        }
        return true;
    }

    public boolean arePermutations(String str1, String str2) {
        if(str1.length()!=str2.length()) return false;

        char[] str1Chars =  str1.toCharArray();
        char[] str2Chars =  str2.toCharArray();
        int str1Rep = 0;
        int str2Rep = 0;
        for(char ch: str1Chars) {
            int shiftDistance = (int)ch - (int)'a';
            int charRep = (1 << shiftDistance);
            str1Rep |= charRep;
        }
        for(char ch: str2Chars) {
            int shiftDistance = (int)ch - (int)'a';
            int charRep = (1 << shiftDistance);
            str2Rep |= charRep;
        }
        return str1Rep == str2Rep;
    }

    private String sort(String str){
        char [] content = str.toCharArray();
        java.util.Arrays.sort(content);
        return new String(content);
    }

    public boolean arePermutations2(String str1, String str2) {
        if (str1.length()!=str2.length()) return false;
        return sort(str1).contentEquals(sort(str2));
    }

    public boolean arePermutations3(String str1, String str2){
        if(str1.length()!=str2.length()) return false;
        int[] checksum = new int [256];
        char[] str1Content = str1.toCharArray();
        char[] str2Content = str2.toCharArray();
        for(char ch : str1Content){
            checksum[(int)ch]++;
        }

        for(char ch: str2Content){
            checksum[(int)ch]--;
            if(checksum[(int)ch]<0) return false;
        }

        for(int sum : checksum){
            if (sum!=0) return false;
        }

        return true;

    }

    private String URLify(String s){
        int length = s.length();
        char[] output = new char[length];
        s = s.trim();
        char[] content = s.toCharArray();
        int j = 0; //output index
        for(int i = 0; i < s.length() ; i++){
            if (content[i] == ' '){
                output[j] = '%';
                j++;
                output[j] = '2';
                j++;
                output[j] = '0';
            } else {
                output[j] = content[i];
            }
            j++;
        }
        return new String(output);
    }

    private void replaceSpaces(char[] content, int trueLength){
        int spaceCount = 0;
        for(int i = 0; i < trueLength; i++){
            if (content[i] == ' '){
                spaceCount++;
            }
        }
        int index = trueLength + spaceCount * 2 - 1;
        for(int i = trueLength - 1; i>0 ; i--){
            if(content[i] == ' '){
                content[index] = '0';
                content[index-1] = '2';
                content[index-2] = '%';
                index = index -3;
            } else {
                content[index] = content[i];
                index--;
            }
        }
    }

    private boolean doesPermutateToPalindrome(String s){
        //To be able to permutate in becoming a palindrome, the string must have same number of characters
        //one character may be single
        s = s.toLowerCase();
        int[] sum = new int[256]; // Assume extended ASCII
        char[] content = s.toCharArray();
        for(char ch : content){
            if(ch == ' ') continue; //disregard space character
            sum[ch]++;
        }
        int oddCount = 0;
        for(int charCount : sum){
            if (charCount % 2 == 1){
                oddCount++;
                if (oddCount>1) return false;
            }
        }

        return true;
    }

    private int getNormalizedCharIndex(char ch){
        int low = 'a';
        int high ='z';
        if(ch >= low && ch <= high ){
            return ch - low;
        } else {
            return -1;
        }
    }

    private int toggleBit(int bitVector, int index){
        int input = 1 << index;
        if((bitVector & input) == 0 ){
            bitVector |= input;
        } else {
            bitVector &= ~input;
        }
        return bitVector;
    }

    private boolean checkOnlyOneBitIsOne(int bitVector){
        return (bitVector & (bitVector - 1)) == 0;
    }

    private boolean isPermutationOfPalindrome(String str) {
        int bitVector = 0;
        char [] content = str.toLowerCase().toCharArray();
        for(char ch : content) {
            int index = getNormalizedCharIndex(ch);
            if(index > 0){
                bitVector = toggleBit(bitVector, index);
            }
        }
        return checkOnlyOneBitIsOne(bitVector);
    }

    //pale
    //ple
    //pales
    //bale


    private boolean diffOne(String str1, String str2){
        if(str1.length() < str2.length()) throw new IllegalArgumentException();
        if(str1.length() - str2.length() > 1) return false;
        boolean diff = false;
        char[] str1Content = str1.toCharArray();
        char[] str2Content = str2.toCharArray();
        int j = 0;
        for(int i = 0; i < str1.length() & j <str2.length(); i++){
            if(str1Content[i] != str2Content[j]) {
                if(!diff) {
                    diff = true;
                } else {
                    return false;
                }
                if(str1.length() > str2.length()) {
                    j--;
                }
            }
            j++;
        }
        return true;
    }
    private boolean isOneAway(String str1, String str2){
        if(str2.length() > str1.length()) return diffOne(str2,str1);
        return diffOne(str1,str2);
    }

    private String compress(String target){
        char[] content = target.toCharArray();
        StringBuilder sb = new StringBuilder();
        char prev = '\0';
        int count = 0;
        for(char c : content) {
            if(prev == '\0') { //initial case
                sb.append(c);
                prev = c;
                count ++;
            } else {
                if (c == prev) {
                    count++;
                } else {
                    sb.append(count);
                    sb.append(c);
                    prev = c;
                    count = 1;
                }
            }
        }
        sb.append(count);
        if(sb.length()>target.length()) {
            return target;
        } else {
            return sb.toString();
        }
    }

    void printMatrix(int[][] matrix){
        int dim = matrix[0].length;
        for(int i=0; i<dim; i++) {
            StringBuilder sb = new StringBuilder();
            for(int j=0; j<dim; j++){
                for(int s=0; s< 4 - String.valueOf(matrix[i][j]).length();s++){
                    sb.append(" ");
                }
                sb.append(matrix[i][j]);
                sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }

    int[][] rotateMatrix(int[][] matrix){
        int dim = matrix[0].length;
        int [][] rotated = new int[dim][dim];
        for(int j=dim-1; j>-1; j--){
            for(int i=0; i<dim; i++){
                rotated[dim-1-j][i] = matrix[i][j];
            }
        }
        return rotated;
    }
    void rotateMatrixInplace(int[][] matrix) {
        int dim = matrix[0].length;
        int [][] memory = new int[dim][dim];
        int counter = 0;
        for(int j=dim-1; j>=0; j--){
             for(int i=0; i<dim; i++){
                if(counter + i + 1 < dim){
                    memory[counter][i] = matrix[counter][i];
                }
                if(memory[i][j]>0){
                    matrix[dim-1-j][i] = memory[i][j];
                } else {
                    matrix[dim-1-j][i] = matrix[i][j];
                }
            }
            counter++;
        }
        System.out.println("______________________________________________________________");
        printMatrix(memory);
        System.out.println("______________________________________________________________");
        return;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new NotesAdapter(new ArrayList<Note>(0), mItemListener);
        mActionsListener = new NotesPresenter(Injection.provideNotesRepository(), this);
        //_____________________________________________________________
//        linkedlist llist = new linkedlist(new int[]{6,2,3,4,9,7,11,4,8});
//        String sllist = llist.print();
//        llist.repositionAll(5);
//        sllist = llist.print();
//
//
//        linkedlist llist2 = new linkedlist(new int[]{3,5,8,5,10,2,1});
//        sllist = llist2.print();
//        llist2.repositionAll(5);
//        sllist = llist2.print();
//
//
//
//
//        linkedlist llist3 = new linkedlist();
//        llist3.add(5);
//        llist3.add(4);
//        llist3.add(5);
//        linkedlist.node n= llist3.add(8);
//        llist3.add(8);
//        llist3.add(6);
//        llist3.add(8);
//        sllist = llist3.print();
//
//        llist3.remove(n);
//        sllist = llist3.print();
//        //llist3.removeDuplicates();
//        llist3.removeDuplicates2();
//        sllist = llist3.print();
//
//
//
//        int[][] matrix = {{ 1, 2, 4 , 19, 21},
//                          { 1, 3, 9 , 17, 32},
//                          { 5, 6, 11 , 19, 44},
//                          { 9, 2, 2 , 88, 99},
//                          { 1, 1, 9 , 91, 2}};
//        System.out.println("______________________________________________________________");
//        printMatrix(matrix);
//        System.out.println("______________________________________________________________");
//        rotateMatrixInplace(matrix);
//        printMatrix(matrix);
//        System.out.println("______________________________________________________________");
//        rotateMatrixInplace(matrix);
//        printMatrix(matrix);
//        System.out.println("______________________________________________________________");
//        rotateMatrixInplace(matrix);
//        printMatrix(matrix);
//        System.out.println("______________________________________________________________");
//        rotateMatrixInplace(matrix);
//        printMatrix(matrix);
//        System.out.println("______________________________________________________________");
//        int [][]rotatedMatrix = rotateMatrix(matrix);
//        rotatedMatrix = rotateMatrix(rotatedMatrix);
//        rotatedMatrix = rotateMatrix(rotatedMatrix);
//        rotatedMatrix = rotateMatrix(rotatedMatrix);
//
//        System.out.println("______________________________________________________________");
//        printMatrix(rotatedMatrix);
//        System.out.println("______________________________________________________________");
//
//        String zipped = compress("aabcccccddddaaaaa");
//        zipped = compress("abccda");
//        boolean bIsOneAway = isOneAway("pale", "ple");
//        bIsOneAway = isOneAway("pales", "pale");
//        bIsOneAway = isOneAway("pale", "bale");
//        bIsOneAway = isOneAway("pale", "bae");
//
//        bIsOneAway = isOneAway("apple", "aple");
//        bIsOneAway = isOneAway("apple", "apples");
//        bIsOneAway = isOneAway("apple", "bpple");
//        bIsOneAway = isOneAway("apple", "appl");
//        bIsOneAway = isOneAway("apple", "pple");
//        bIsOneAway = isOneAway("apple", "appe");
//        bIsOneAway = isOneAway("apple", "appxe");
//        bIsOneAway = isOneAway("apple", "appre");
//
//        boolean b = doesPermutateToPalindrome("Tact Coa");
//        b = isPermutationOfPalindrome("Tact Coa");
//        b = isPermutationOfPalindrome("Tacts Coa");
//        char[] urlInput = "Mr John Smith    ".toCharArray();
//
//        replaceSpaces(urlInput,13);
//        String urlified = URLify("Mr John Smith    ");
//
//        boolean arePermutations = arePermutations("fuck" , "fcuk");
//        arePermutations = arePermutations("tcuk" , "fcuk");
//        arePermutations = arePermutations2("tcuk" , "fcuk");
//        arePermutations = arePermutations2("fcuk" , "fcuk");
//        arePermutations = arePermutations3("tcuk" , "fcuk");
//        arePermutations = arePermutations3("fcuk" , "fcuk");
//
//        boolean unique = isUnique("ABCD");
//        unique = isUniqueChars("abaq");
//        unique = isUniqueChars("dac");
//        unique = isUnique("ABAQ");
//        unique = isUnique("DAC");
//        unique = isUnique2("ABAQ");
//        unique = isUnique2("DAC");
//        ArrayList<String> permutations = permutation("ABC");
//        permutations= permutation("ABCD");
        //_____________________________________________________________
    }

    @Override
    public void onResume() {
        super.onResume();
        mActionsListener.loadNotes(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setRetainInstance(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If a note was successfully added, show snackbar
        if (REQUEST_ADD_NOTE == requestCode && Activity.RESULT_OK == resultCode) {
            Snackbar.make(getView(), getString(R.string.successfully_saved_note_message),
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notes, container, false);
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.notes_list);
        recyclerView.setAdapter(mListAdapter);

        int numColumns = getContext().getResources().getInteger(R.integer.num_notes_columns);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab_add_notes);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.addNewNote();
            }
        });

        // Pull-to-refresh
        SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.loadNotes(true);
            }
        });
        return root;
    }

    /**
     * Listener for clicks on notes in the RecyclerView.
     */
    NoteItemListener mItemListener = new NoteItemListener() {
        @Override
        public void onNoteClick(Note clickedNote) {
            mActionsListener.openNoteDetails(clickedNote);
        }
    };

    @Override
    public void setProgressIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showNotes(List<Note> notes) {
        mListAdapter.replaceData(notes);
    }

    @Override
    public void showAddNote() {
        Intent intent = new Intent(getContext(), AddNoteActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NOTE);
    }

    @Override
    public void showNoteDetailUi(String noteId) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        Intent intent = new Intent(getContext(), NoteDetailActivity.class);
        intent.putExtra(NoteDetailActivity.EXTRA_NOTE_ID, noteId);
        startActivity(intent);
    }


    private static class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

        private List<Note> mNotes;
        private NoteItemListener mItemListener;

        public NotesAdapter(List<Note> notes, NoteItemListener itemListener) {
            setList(notes);
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View noteView = inflater.inflate(R.layout.item_note, parent, false);

            return new ViewHolder(noteView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Note note = mNotes.get(position);

            viewHolder.title.setText(note.getTitle());
            viewHolder.description.setText(note.getDescription());
        }

        public void replaceData(List<Note> notes) {
            setList(notes);
            notifyDataSetChanged();
        }

        private void setList(List<Note> notes) {
            mNotes = checkNotNull(notes);
        }

        @Override
        public int getItemCount() {
            return mNotes.size();
        }

        public Note getItem(int position) {
            return mNotes.get(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public TextView title;

            public TextView description;
            private NoteItemListener mItemListener;

            public ViewHolder(View itemView, NoteItemListener listener) {
                super(itemView);
                mItemListener = listener;
                title = (TextView) itemView.findViewById(R.id.note_detail_title);
                description = (TextView) itemView.findViewById(R.id.note_detail_description);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Note note = getItem(position);
                mItemListener.onNoteClick(note);

            }
        }
    }

    public interface NoteItemListener {

        void onNoteClick(Note clickedNote);
    }

}
