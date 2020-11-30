export interface MenuItem {
  label: string; // Button label
  icon: string; // Material icon name
  link: string; // RouterLink to navigate to
  login: boolean; // true: only show if logged in, false: only show if logged out, null: always show
}
