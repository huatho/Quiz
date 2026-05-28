import { useState } from 'react'
import { getApiMessage } from '../api/httpClient'
import { login } from '../api/authApi'

function LoginPage({ onLoginSuccess }) {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  const updateField = (event) => {
    setForm((currentForm) => ({
      ...currentForm,
      [event.target.name]: event.target.value,
    }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setError('')
    setSuccess('')

    if (!form.email.trim() || !form.password) {
      setError('Please enter your email and password.')
      return
    }

    setIsSubmitting(true)

    try {
      const account = await login({
        email: form.email.trim(),
        password: form.password,
      })
      setSuccess('Login successful.')
      onLoginSuccess(account)
    } catch (err) {
      setError(getApiMessage(err, 'Login failed.'))
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <form className="space-y-5" onSubmit={handleSubmit}>
      <div>
        <label className="text-sm font-medium text-slate-700" htmlFor="email">
          Email address
        </label>
        <input
          id="email"
          name="email"
          type="email"
          value={form.email}
          onChange={updateField}
          className="input-control mt-2"
          autoComplete="email"
          required
        />
      </div>

      <div>
        <label className="text-sm font-medium text-slate-700" htmlFor="password">
          Password
        </label>
        <input
          id="password"
          name="password"
          type="password"
          value={form.password}
          onChange={updateField}
          className="input-control mt-2"
          autoComplete="current-password"
          required
        />
      </div>

      {error && <p className="text-sm text-red-600">{error}</p>}
      {success && <p className="text-sm text-teal-700">{success}</p>}

      <button
        type="submit"
        disabled={isSubmitting}
        className="btn btn-primary w-full"
      >
        {isSubmitting ? 'Signing in...' : 'Sign in'}
      </button>
    </form>
  )
}

export default LoginPage
