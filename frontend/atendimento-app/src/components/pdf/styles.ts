import { StyleSheet } from "@react-pdf/renderer";

export const styles = StyleSheet.create({
  page: {
    padding: 30,
    fontSize: 9,
    fontFamily: "Helvetica",
  },

  header: {
    flexDirection: "row",
    marginBottom: 20,
  },

  logo: {
    width: 100,
    height: 100,
    marginRight: 30,
  },

  headerInfo: {
    justifyContent: "center",
    alignItems: "center",
    fontSize: 9,
  },

  apae: {
    fontSize: 11,
    fontWeight: "bold",
  },

  section: {
    marginBottom: 20,
  },

  field: {
    marginBottom: 4,
    marginLeft: 20,
  },

  title: {
    fontSize: 11,
    fontWeight: "bold",
    marginBottom: 20,
    textAlign: "center",
  },

  description: {
    marginBottom: 50,
    textAlign: "justify",
    lineHeight: 1,
  },

  footer: {
    fontSize: 10,
    textAlign: "right",
  },
});
