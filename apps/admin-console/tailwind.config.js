/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"] ,
  theme: {
    extend: {
      colors: {
        ink: "#101828",
        surface: "#F9F7F1",
        accent: "#EE6C4D",
        moss: "#2F5233",
        sand: "#F2E6CE",
        steel: "#475467"
      },
      fontFamily: {
        display: ["Space Grotesk", "sans-serif"],
        body: ["Source Sans 3", "sans-serif"],
      }
    },
  },
  plugins: [],
};
