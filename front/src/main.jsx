import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
// import "./index.css";
import App from "./App.jsx";
import { Provider } from "react-redux";
import { PersistGate } from "redux-persist/integration/react";
import { store, persistor } from "./helpers/redux/store.js";
import { BrowserRouter } from "react-router";
import { PrimeReactProvider } from "primereact/api";

createRoot(document.getElementById("root")).render(
    <StrictMode>
        <BrowserRouter>
            <Provider store={store}>
                <PersistGate loading={null} persistor={persistor}>
                    <PrimeReactProvider>
                        <App />
                    </PrimeReactProvider>
                </PersistGate>
            </Provider>
        </BrowserRouter>
    </StrictMode>
);
