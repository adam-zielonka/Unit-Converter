package pro.adamzielonka.converter.activities.edit;

import pro.adamzielonka.converter.activities.abstractes.ListActivity;

public class AddMeasureActivity extends ListActivity {
    @Override
    public void onUpdate() {

    }

    @Override
    public void addItems() {

    }

//    private View addByCreateView;
//    private View addFromFileView;
//    private View getFileView;
//    private View addFromCloudView;
//
//    Measure userMeasure;
//    ConcreteMeasure concreteMeasure;
//
//    @Override
//    public void onLoad() throws Exception {
//        setTitle(R.string.title_activity_add_measure);
//        super.onLoad();
//        itemsView.setActivity(this);
//        itemsView.setEmptyAdapter();
//        itemsView.setOnItemClickListener(this);
//
//        itemsView.addItemTitle(getString(R.string.list_add_measure));
//        addByCreateView = itemsView.addItem(getString(R.string.list_item_create), getString(R.string.list_item_create_description));
//        addFromFileView = itemsView.addItem(getString(R.string.list_item_load_from_json), getString(R.string.list_item_load_from_json_description));
//        getFileView = itemsView.addItem(getString(R.string.list_item_json_repo), getString(R.string.list_item_json_repo_description));
//        addFromCloudView = itemsView.addItem(getString(R.string.list_item_load_form_cloud), getString(R.string.list_item_load_form_cloud_description));
//    }
//
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//        if (view.equals(addByCreateView)) {
//            EditText editText = getDialogEditText("");
//            getAlertDialogSave(R.string.dialog_measure_name, editText.getRootView(), (dialog, which) -> {
//                userMeasure = new Measure();
//                userMeasure.setName(getLangCode(this), editText.getText().toString());
//                userMeasure.global = getLangCode(this);
//                concreteMeasure = userMeasure.getConcreteMeasure();
//
//                String concreteFileName = getNewFileInternalName(this,
//                        "concrete_", concreteMeasure.getName(getLangCode(this)));
//                String userFileName = getNewFileInternalName(this,
//                        "user_", concreteMeasure.getName(getLangCode(this)));
//
//                concreteMeasure.concreteFileName = concreteFileName;
//                concreteMeasure.userFileName = userFileName;
//                try {
//                    saveMeasure(this, concreteMeasure, userMeasure);
//                    setResultCode(RESULT_OK);
//                } catch (Exception e) {
//                    showError(this, R.string.error_could_not_save_changes);
//                }
//                Intent intent = new Intent(getApplicationContext(), EditMeasureActivity.class);
//                intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.concreteFileName);
//                startActivityForResult(intent, REQUEST_EDIT_ACTIVITY);
//            }).show();
//
//        } else if (view.equals(addFromFileView)) {
//            ActivityCompat.requestPermissions(this,
//                    getReadAndWritePermissionsStorage(), REQUEST_ADD_FROM_FILE);
//
//        } else if (view.equals(getFileView)) {
//            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://bitbucket.org/adam-zielonka-pro/converters/src"));
//            startActivity(browserIntent);
//
//        } else if (view.equals(addFromCloudView)) {
//            Intent intent = new Intent(getApplicationContext(), CloudActivity.class);
//            startActivity(intent);
//
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
//        if (requestCode == REQUEST_ADD_FROM_FILE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("*/*");
//                startActivityForResult(intent, RESULT_ADD_FROM_FILE);
//            } else {
//                showError(this, R.string.error_no_permissions);
//            }
//        }
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
//        Log.i("RESULT", "onActivityResult: " + requestCode + " " + resultCode);
//        if (requestCode == RESULT_ADD_FROM_FILE && resultCode == Activity.RESULT_OK) {
//            Uri uri;
//            if (resultData != null) {
//                uri = resultData.getData();
//                addConverterFromFile(uri);
//            }
//        } else if (requestCode == REQUEST_EDIT_ACTIVITY) {
//            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//            intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.concreteFileName);
//            startActivity(intent);
//            finish();
//        } else {
//            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//            startActivity(intent);
//            finish();
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//    private void addConverterFromFile(Uri uri) {
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(openFileToInputStream(this, uri)));
//
//            Gson gson = getGson();
//            Measure userMeasure = gson.fromJson(reader, Measure.class);
//            ConcreteMeasure concreteMeasure = userMeasure.getConcreteMeasure();
//
//            if (!concreteMeasure.isCorrect()) {
//                showError(this, R.string.error_no_units);
//                return;
//            }
//
//            String concreteFileName = getNewFileInternalName(this, "concrete_", concreteMeasure.getName(getLangCode(this)));
//            String userFileName = getNewFileInternalName(this, "user_", concreteMeasure.getName(getLangCode(this)));
//
//            concreteMeasure.concreteFileName = concreteFileName;
//            concreteMeasure.userFileName = userFileName;
//
//            saveToInternal(this, concreteFileName, gson.toJson(concreteMeasure));
//            saveToInternal(this, userFileName, gson.toJson(userMeasure));
//
//            Intent intent = new Intent(getApplicationContext(), StartActivity.class);
//            intent.putExtra(EXTRA_MEASURE_FILE_NAME, concreteMeasure.concreteFileName);
//            startActivity(intent);
//            finish();
//        } catch (FileNotFoundException e) {
//            showError(this, R.string.error_no_file);
//        } catch (IOException e) {
//            showError(this, R.string.error_no_json_file);
//        } catch (Exception e) {
//            showError(this, R.string.error_no_json_file);
//        }
//    }
}
