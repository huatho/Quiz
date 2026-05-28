import { useState } from 'react'
import { clearAuthTokens, getAccessToken, getApiMessage } from './api/httpClient'
import { refreshToken } from './api/authApi'
import DashboardPage from './pages/DashboardPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'

function VerificationPendingView({ email, onBackToLogin, onRegisterAgain }) {
  return (
    <div className="space-y-6 text-center">
      <div>
        <p className="text-sm font-semibold text-teal-700">Email verification</p>
        <h1 className="mt-2 text-2xl font-semibold tracking-normal">
          Check your inbox
        </h1>
        <p className="mt-3 text-sm leading-6 text-slate-600">
          We sent a verification link to{' '}
          <span className="font-semibold text-slate-900">{email}</span>. Open that
          link to activate your account.
        </p>
      </div>

      <div className="rounded-lg border border-teal-100 bg-teal-50 px-4 py-3 text-left text-sm leading-6 text-teal-900">
        After verification, the backend will redirect you to the verification
        result page. You can sign in once the account is verified.
      </div>

      <div className="grid gap-3 sm:grid-cols-2">
        <button type="button" onClick={onBackToLogin} className="btn btn-primary">
          Back to login
        </button>
        <button
          type="button"
          onClick={onRegisterAgain}
          className="btn btn-secondary"
        >
          Use another email
        </button>
      </div>
    </div>
  )
}

function App() {
  const [activeView, setActiveView] = useState('login')
  const [isAuthenticated, setIsAuthenticated] = useState(Boolean(getAccessToken()))
  const [sessionMessage, setSessionMessage] = useState('')
  const [registeredEmail, setRegisteredEmail] = useState('')
  const [isRefreshingSession, setIsRefreshingSession] = useState(false)
  const isLogin = activeView === 'login'
  const isRegister = activeView === 'register'
  const isVerificationPending = activeView === 'verificationPending'
  const pathname = window.location.pathname

  const handleLoginSuccess = () => {
    setIsAuthenticated(true)
    setSessionMessage('You are ready to start practicing quizzes.')
  }

  const handleRegisterSuccess = (email) => {
    setRegisteredEmail(email)
    setActiveView('verificationPending')
  }

  const handleLogout = () => {
    clearAuthTokens()
    setIsAuthenticated(false)
    setSessionMessage('')
  }

  const handleRefreshSession = async () => {
    setSessionMessage('')
    setIsRefreshingSession(true)

    try {
      await refreshToken()
      setIsAuthenticated(true)
      setSessionMessage('Session refreshed successfully.')
    } catch (err) {
      setIsAuthenticated(false)
      setSessionMessage(getApiMessage(err, 'Session refresh failed. Please sign in again.'))
    } finally {
      setIsRefreshingSession(false)
    }
  }

  if (pathname === '/verify-email/success' || pathname === '/verify-email/failed') {
    const isSuccess = pathname.endsWith('/success')

    return (
      <main className="app-bg flex min-h-screen items-center justify-center px-4 py-10 text-slate-950">
        <section className="surface-card w-full max-w-md rounded-xl p-6 text-center">
          <p
            className={`text-sm font-semibold ${
              isSuccess ? 'text-teal-700' : 'text-red-600'
            }`}
          >
            Email verification
          </p>
          <h1 className="mt-2 text-2xl font-semibold tracking-normal">
            {isSuccess ? 'Verification successful' : 'Verification failed'}
          </h1>
          <p className="mt-3 text-sm leading-6 text-slate-600">
            {isSuccess
              ? 'Your account is now verified. You can sign in and start managing quizzes.'
              : 'The verification link is invalid or expired. Please register again or request a new email from the backend flow.'}
          </p>
          <button
            type="button"
            onClick={() => window.location.assign('/')}
            className="btn btn-primary mt-6"
          >
            Back to login
          </button>
        </section>
      </main>
    )
  }

  if (isAuthenticated) {
    return (
      <>
        {sessionMessage && (
          <div className="toast-card fixed left-1/2 top-4 z-50 w-[calc(100%-2rem)] max-w-xl -translate-x-1/2 rounded-xl border border-teal-200 bg-teal-50 px-4 py-3 text-sm font-medium text-teal-800 shadow-lg shadow-teal-950/10">
            {sessionMessage}
          </div>
        )}
        <DashboardPage
          onLogout={handleLogout}
          onRefreshSession={handleRefreshSession}
          isRefreshingSession={isRefreshingSession}
        />
      </>
    )
  }

  return (
    <main className="app-bg flex min-h-screen items-center justify-center px-4 py-10 text-slate-950">
      <section className="surface-card w-full max-w-md rounded-xl p-6">
        <div className="mb-6">
          <p className="text-sm font-medium text-teal-700">Quiz Online</p>
          {!isVerificationPending && (
            <>
              <h1 className="mt-2 text-2xl font-semibold tracking-normal">
                {isLogin ? 'Sign in to your account' : 'Create your account'}
              </h1>
              <p className="mt-2 text-sm leading-6 text-slate-600">
                {isLogin
                  ? 'Continue learning with your saved quiz progress.'
                  : 'Create an account to save scores and quiz history.'}
              </p>
            </>
          )}
        </div>

        {!isVerificationPending && (
          <div className="mb-6 grid grid-cols-2 rounded-lg bg-slate-100 p-1">
            <button
              type="button"
              onClick={() => setActiveView('login')}
              className={`tab-button ${
                isLogin ? 'bg-white text-slate-950 shadow-sm' : 'text-slate-600 hover:bg-white/70'
              }`}
            >
              Login
            </button>
            <button
              type="button"
              onClick={() => setActiveView('register')}
              className={`tab-button ${
                isRegister ? 'bg-white text-slate-950 shadow-sm' : 'text-slate-600 hover:bg-white/70'
              }`}
            >
              Register
            </button>
          </div>
        )}

        {isVerificationPending ? (
          <VerificationPendingView
            email={registeredEmail}
            onBackToLogin={() => setActiveView('login')}
            onRegisterAgain={() => setActiveView('register')}
          />
        ) : isLogin ? (
          <LoginPage onLoginSuccess={handleLoginSuccess} />
        ) : (
          <RegisterPage onRegisterSuccess={handleRegisterSuccess} />
        )}
      </section>
    </main>
  )
}

export default App
