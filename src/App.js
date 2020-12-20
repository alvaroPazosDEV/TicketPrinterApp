import React from 'react'
import 'react-native-gesture-handler'
import { setCustomText } from 'react-native-global-props'
import normalize from 'react-native-normalize'
import Navigation from './navigation'

setCustomText({
  style: {
    fontFamily: 'OpenSans-Regular',
    fontSize: normalize(18),
    textAlign: 'center',
    margin: normalize(3)
  }
})

const App = () => {
  return (
    <Navigation />
  )
}

export default App