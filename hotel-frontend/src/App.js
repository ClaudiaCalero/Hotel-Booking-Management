import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Navbar from "./component/common/Navbar";
import Footer from "./component/common/Footer";


function App() {
  return (
    <BrowserRouter>
      <div className="App">
        <Navbar />
        <div className="content">
          <Routes>

          </Routes>
        </div>
        <Footer />
      </div>
    </BrowserRouter>
  );
}

export default App;
