import AllRoute from "./components/AllRoute";
import { NotificationContainer } from "./components/ui/notification";
import { LoadingOverlay } from "./components/ui/loading";

function App() {
  return (
    <>
      <AllRoute />
      <NotificationContainer />
      <LoadingOverlay />
    </>
  );
}

export default App;
