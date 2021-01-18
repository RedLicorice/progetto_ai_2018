// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  api_url: 'http://localhost:8080/',
  trusted_url: 'http://trusted-app:secret@localhost:8080/oauth/token',
  register_url: 'http://register-app:secret@localhost:8080/oauth/token',
  refresh_url: 'http://register-app:secret@localhost:8080/oauth/token',
  archives_url: 'http://localhost:8080/archives',
  archives_uploaded_url: 'http://localhost:8080/archives/upload', // GET
  archives_upload_url: 'http://localhost:8080/archives/upload', // POST
  archives_download_url: 'http://localhost:8080/archives/download/{id}',
  archives_public_url: 'http://localhost:8080/archives/public/{id}',
  archives_search_url: 'http://localhost:8080/archives/search',
  archives_buy_url: 'http://localhost:8080/archives/buy',
  store_invoices_url: 'http://localhost:8080/store/invoices',
  store_invoice_detail_url: 'http://localhost:8080/store/invoices/{id}',
  store_invoice_pay_url: 'http://localhost:8080/store/invoices/{id}',
  store_invoice_cancel_url: 'http://localhost:8080/store/invoices/{id}'
};

/*
 * In development mode, to ignore zone related error stack frames such as
 * `zone.run`, `zoneDelegate.invokeTask` for easier debugging, you can
 * import the following file, but please comment it out in production mode
 * because it will have performance impact when throw error
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
