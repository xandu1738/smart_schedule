import { configureStore } from '@reduxjs/toolkit'
import { persistStore, persistReducer } from 'redux-persist'
import storage from 'redux-persist/lib/storage'
import { combineReducers } from '@reduxjs/toolkit'
import authSlice from './slices/authSlice'
import { baseApiSlice } from './slices/baseApiSlice'

// Redux persist configuration
const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['auth'], // Only persist auth slice
}

// Combine reducers
const rootReducer = combineReducers({
  auth: authSlice.reducer,
  [baseApiSlice.reducerPath]: baseApiSlice.reducer,
})

// Create persisted reducer
const persistedReducer = persistReducer(persistConfig, rootReducer)

/**
 * The Redux store.
 * 
 * @type {import("redux").Store}
 */
export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE'],
      },
    }).concat(baseApiSlice.middleware),
})

export const persistor = persistStore(store)