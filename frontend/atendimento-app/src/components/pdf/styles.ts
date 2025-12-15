import { StyleSheet } from "@react-pdf/renderer";

export const styles = StyleSheet.create({
  page: {
    padding: 30,
    fontSize: 12,
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
    marginTop: 30,
  },

  headerInfo: {
    justifyContent: "center",
  },

  apae: {
    fontSize: 14,
    fontWeight: "bold",
  },

  section: {
    marginBottom: 20,
  },

  field: {
    marginBottom: 4,
  },

  title: {
    fontSize: 16,
    fontWeight: "bold",
    marginBottom: 10,
    textAlign: "center",
  },

  description: {
    marginBottom: 30,
    textAlign: "justify",
    lineHeight: 1.5,
  },

  footer: {
    marginTop: "auto",
    fontSize: 10,
    textAlign: "right",
  },
});
