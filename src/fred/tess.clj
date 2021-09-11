(ns fred.tess
  (:import net.sourceforge.tess4j.Tesseract)
  (:import java.io.File))

(use 'debux.core)
;; settings
;(def tesseract-data-dir "/usr/share/tessdata")
(def tesseract-data-dir "/usr/share/tesseract-ocr/4.0.0/tessdata")
(def language "eng")
(def test-file "eurotext.png")

#_ (defn prepare-tesseract [data-path]
    (let [t (. Tesseract getInstance)]
      (.setDatapath t data-path)
      t))

(defn prepare-tesseract [data-path]
  (let [t (new Tesseract)]
    (.setDatapath t data-path)
    t))

(defn ocr [t lang img]
  (.setLanguage t lang)
  (.doOCR t img))

(comment

  (dbgn (let [tesseract (prepare-tesseract tesseract-data-dir)
              result (ocr tesseract language (new File test-file))]
          (println result)))

  ,)


