import React, { useEffect, useState } from 'react'
import { Linking, Text, View, StyleSheet, NativeModules } from 'react-native'
import normalize from 'react-native-normalize'
import Barcode from 'react-native-barcode-builder'
import colors from '../styles/colors'
import { ScrollView } from 'react-native-gesture-handler'

const Home = () => {

  const [url, setUrl] = useState(null)
  const [hasPrinter, setHasPrinter] = useState(true)

  useEffect(() => {
    checkPrinterAvailable()
    setInitialUrl()
    Linking.addListener('url', handleOpenUrl)
  }, [])

  const setInitialUrl = async () => {
    const url = await Linking.getInitialURL()
    if(url) setUrl(url)
  }

  const handleOpenUrl = ({ url }) => {
    console.log('Open url ', url)
    setUrl(url)
  }

  const checkPrinterAvailable = async () => {
    try {
      await NativeModules.PrinterModule.getStatus()
      setHasPrinter(true)
    } catch(e) {
      console.log('No se detectó impresora')
    }
  }

  const renderContent = () => {
    return !url ? (
      <View style={styles.empty}>
        <Text>No se ha cargado un ticket de regalo</Text>
      </View>
    ) : renderTicket()
  }

  const decodeParams = () => {
    try {
      const params = url.split('@')
      const skus = params[9].split(',')
      return {
        sucursalC: params[1],
        sucursalS: params[2],
        caja: params[3],
        fecha: params[4],
        transaccion: params[5],
        boleta: params[6],
        barcode: params[7],
        unidades: params[8],
        skus
      }
    } catch (e) {
      console.log(e)
      return null
    }
  }

  const renderTicket = () => {
    const data = decodeParams()
    if(!data) {
      return (
        <View style={styles.empty}>
          <Text>El ticket cargado tiene un formato{"\n"}inválido.</Text>
        </View>
      )
    }
    const { sucursalC, sucursalS, caja, fecha, transaccion, boleta, barcode, unidades, skus } = data
    return (
      (
        <ScrollView>
          <View style={styles.ticket}>
            <Text style={styles.title}>TICKET DE REGALO</Text>
            <Text>Para cambio o servicio técnico,{"\n"}debe presentar este documento</Text>
            <Text style={styles.space}>COMERCIO/SUCURSAL: {sucursalC}</Text>
            <Text>{sucursalS}/{caja} {fecha} {transaccion}</Text>
            <Text style={styles.space}>BOLETA REFERENCIA: {boleta}</Text>
            <Barcode
              value={barcode}
              format="CODE128"
              width={normalize(4, 'width')}
              height={normalize(90, 'height')}
            />
            <Text style={{ marginTop: -10 }}>{barcode}</Text>
            <Text style={styles.space}>Ripley</Text>
            <Text>NRO. DE UNIDADES: {unidades}</Text>
            <View style={styles.doublespace}>
              {
                skus.map(sku => (
                  <Text>{sku}</Text>
                ))
              }
            </View>
          </View>
        </ScrollView>
      )
    )
  }

  return (
    <View style={styles.container}>
      <View style={styles.body}>
        {renderContent()}
      </View>
      <View style={[styles.button, !(hasPrinter && url) ? styles.disabled : null]}>
        <Text style={styles.buttonText}>IMPRIMIR TICKET</Text>
      </View>
    </View>
  )

}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
    padding: normalize(10),
  },
  body: {
    flex: 1,
    justifyContent: 'center'
  },
  button: {
    backgroundColor: colors.ALT_VIOLETA,
    padding: normalize(12),
    borderRadius: normalize(10),
    alignItems: 'center'
  },
  disabled: {
    backgroundColor: colors.TEXTOS_TERCIARIO,
    opacity: 0.5
  },
  buttonText: {
    color: colors.TEXTOS_NEGATIVO,
    fontFamily: 'OpenSans-Bold'
  },
  empty: {
    alignItems: 'center',
  },
  ticket: {
    flex: 1,
    alignItems: 'center',
    paddingHorizontal: normalize(20),
    paddingTop: normalize(20)
  },
  title: {
    fontSize: normalize(22),
    marginBottom: normalize(4)
  },
  space: {
    marginTop: normalize(20)
  },
  doublespace: {
    marginVertical: normalize(20)
  }
})

export default Home