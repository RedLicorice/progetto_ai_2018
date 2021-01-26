export const environment = {
  // ToDo: Replace 'localhost' with backend's hostname.
  // For demonstration purposes we keep localhost, but this should really be the final hostname.
  production: true,
  client_credentials: 'trusted-app:secret',
  api_url: 'http://localhost:8080/',
  trusted_url: 'http://trusted-app:secret@localhost:8080/oauth/token',
  register_url: 'http://register-app:secret@localhost:8080/oauth/token',
  refresh_url: 'http://register-app:secret@localhost:8080/oauth/token',
  user_url: 'http://localhost:8080/me',
  user_topup_url: 'http://localhost:8080/topup',
  user_changepw_url: 'http://localhost:8080/change-password',
  archives_url: 'http://localhost:8080/archives',
  archives_uploaded_url: 'http://localhost:8080/archives/upload', // GET
  archives_upload_url: 'http://localhost:8080/archives/upload', // POST
  archives_download_url: 'http://localhost:8080/archives/download/{id}',
  archives_public_url: 'http://localhost:8080/archives/public/{id}',
  archives_delete_url: 'http://localhost:8080/archives/{id}',
  archives_search_url: 'http://localhost:8080/archives/search',
  archives_buy_url: 'http://localhost:8080/archives/buy',
  store_invoices_url: 'http://localhost:8080/store/invoices',
  store_invoice_detail_url: 'http://localhost:8080/store/invoices/{id}',
  store_invoice_pay_url: 'http://localhost:8080/store/invoices/{id}',
  store_invoice_cancel_url: 'http://localhost:8080/store/invoices/{id}',
};
