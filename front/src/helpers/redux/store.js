import { configureStore } from '@reduxjs/toolkit'
import authSlice from './slices/authSlice'
import { baseApiSlice } from './slices/baseApiSlice'

/**
 * The Redux store.
 * 
 * @type {import("redux").Store}
 */
export const store = configureStore({
  reducer: {
    auth: authSlice.reducer,
    [baseApiSlice.reducerPath]: baseApiSlice.reducer,
  },
  middleware: (getDefaultMiddleWare) => getDefaultMiddleWare().concat(baseApiSlice.middleware),
})