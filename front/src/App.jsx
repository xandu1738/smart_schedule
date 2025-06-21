import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {AxiosConfiguration} from './helpers/axios_helper'

function App() {
  const [count, setCount] = useState(0)

  AxiosConfiguration.initialize()

  return (
    <>
      
    </>
  )
}

export default App
